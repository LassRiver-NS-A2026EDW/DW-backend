package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.response.BookPdfResource;
import com.lassriver.bookworm.dtos.response.BookPdfResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.BookPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookPdfServiceImpl implements BookPdfService {

    private static final String LOAN_ACTIVE = "ACTIVE";
    private static final long DEFAULT_MAX_PDF_BYTES = 50L * 1024L * 1024L;
    private static final int MAX_REMOTE_PDF_REDIRECTS = 3;

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Value("${bookworm.storage.pdf-dir:uploads/pdfs}")
    private String pdfDirectory;

    @Value("${bookworm.storage.max-pdf-bytes:52428800}")
    private long maxPdfBytes;

    @Value("${bookworm.storage.allowed-pdf-hosts:}")
    private String allowedPdfHosts;

    @Override
    @Transactional
    public BookPdfResponse uploadPdf(Long bookId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("El archivo PDF es obligatorio.");
        }
        validatePdfMetadata(file.getOriginalFilename(), file.getContentType(), file.getSize());

        Book book = getBook(bookId);
        String filename = buildPdfFilename(book, file.getOriginalFilename());
        Path target = resolveStoragePath(filename);

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            replaceBookPdf(book, target, filename, null);
        } catch (IOException ex) {
            throw new BusinessRuleException("No se pudo guardar el PDF.");
        }

        return toResponse(book, "PDF subido y guardado exitosamente.");
    }

    @Override
    @Transactional
    public BookPdfResponse downloadPdfFromUrl(Long bookId, String url) {
        String cleanUrl = url == null ? "" : url.trim();
        URI currentUri = validateRemotePdfUri(cleanUrl);

        Book book = getBook(bookId);
        String filename = buildPdfFilename(book, cleanUrl);
        Path target = resolveStoragePath(filename);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        try {
            HttpResponse<InputStream> response = sendRemotePdfRequest(client, currentUri);
            for (int redirects = 0; isRedirect(response.statusCode()) && redirects < MAX_REMOTE_PDF_REDIRECTS; redirects++) {
                closeQuietly(response.body());
                currentUri = resolveRedirectUri(currentUri, response);
                response = sendRemotePdfRequest(client, currentUri);
            }

            if (isRedirect(response.statusCode())) {
                closeQuietly(response.body());
                throw new BusinessRuleException("La descarga del PDF excede el limite de redirecciones permitido.");
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                closeQuietly(response.body());
                throw new BusinessRuleException("No se pudo descargar el PDF. Estado remoto: " + response.statusCode());
            }
            String contentType = response.headers().firstValue("Content-Type").orElse("");
            validatePdfMetadata(cleanUrl, contentType, response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(0L));

            try (InputStream input = response.body()) {
                copyWithLimit(input, target, maxPdfBytes > 0 ? maxPdfBytes : DEFAULT_MAX_PDF_BYTES);
            }
            replaceBookPdf(book, target, filename, currentUri.toString());
        } catch (BusinessRuleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessRuleException("Error al descargar el PDF.");
        }

        return toResponse(book, "PDF descargado y guardado exitosamente.");
    }

    private HttpResponse<InputStream> sendRemotePdfRequest(HttpClient client, URI uri) throws IOException, InterruptedException {
        URI safeUri = validateRemotePdfUri(uri.toString());
        HttpRequest request = HttpRequest.newBuilder(safeUri)
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    private URI validateRemotePdfUri(String rawUrl) {
        URI uri;
        try {
            uri = new URI(rawUrl == null ? "" : rawUrl.trim());
        } catch (URISyntaxException ex) {
            throw new BusinessRuleException("URL invalida para descargar el PDF.");
        }

        String scheme = uri.getScheme();
        if (!"https".equalsIgnoreCase(scheme)) {
            throw new BusinessRuleException("URL invalida. La descarga remota solo permite HTTPS.");
        }
        if (uri.getUserInfo() != null || uri.getFragment() != null) {
            throw new BusinessRuleException("URL invalida para descargar el PDF.");
        }
        if (uri.getPort() != -1 && uri.getPort() != 443) {
            throw new BusinessRuleException("URL invalida. Solo se permite el puerto HTTPS por defecto.");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new BusinessRuleException("URL invalida. Debe incluir un host publico permitido.");
        }

        String safeHost = normalizeHost(host);
        if (!allowedPdfHostSet().contains(safeHost)) {
            throw new BusinessRuleException("Host no permitido para descarga remota de PDFs.");
        }
        validatePublicHostResolution(safeHost);

        try {
            String path = uri.getPath() == null || uri.getPath().isBlank() ? "/" : uri.getPath();
            return new URI("https", null, safeHost, uri.getPort(), path, uri.getQuery(), null);
        } catch (URISyntaxException ex) {
            throw new BusinessRuleException("URL invalida para descargar el PDF.");
        }
    }

    private Set<String> allowedPdfHostSet() {
        Set<String> hosts = Arrays.stream((allowedPdfHosts == null ? "" : allowedPdfHosts).split(","))
                .map(String::trim)
                .filter(host -> !host.isBlank())
                .map(this::normalizeHost)
                .collect(Collectors.toUnmodifiableSet());

        if (hosts.isEmpty()) {
            throw new BusinessRuleException("La descarga remota de PDFs no esta configurada para ningun host permitido.");
        }
        return hosts;
    }

    private String normalizeHost(String host) {
        return IDN.toASCII(host.trim().toLowerCase(Locale.ROOT));
    }

    private void validatePublicHostResolution(String host) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            if (addresses.length == 0) {
                throw new BusinessRuleException("No se pudo resolver el host remoto.");
            }
            for (InetAddress address : addresses) {
                if (isUnsafeRemoteAddress(address)) {
                    throw new BusinessRuleException("El host remoto no puede resolver a una red privada o local.");
                }
            }
        } catch (IOException ex) {
            throw new BusinessRuleException("No se pudo resolver el host remoto.");
        }
    }

    private boolean isUnsafeRemoteAddress(InetAddress address) {
        if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return true;
        }

        byte[] bytes = address.getAddress();
        if (address instanceof Inet4Address) {
            int first = bytes[0] & 0xff;
            int second = bytes[1] & 0xff;
            return first == 0
                    || first == 10
                    || first == 127
                    || (first == 100 && second >= 64 && second <= 127)
                    || (first == 169 && second == 254)
                    || (first == 172 && second >= 16 && second <= 31)
                    || (first == 192 && second == 168)
                    || (first == 198 && (second == 18 || second == 19))
                    || first >= 224;
        }

        if (address instanceof Inet6Address) {
            int first = bytes[0] & 0xff;
            return (first & 0xfe) == 0xfc;
        }

        return true;
    }

    private boolean isRedirect(int statusCode) {
        return statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307 || statusCode == 308;
    }

    private URI resolveRedirectUri(URI currentUri, HttpResponse<?> response) {
        String location = response.headers()
                .firstValue("Location")
                .orElseThrow(() -> new BusinessRuleException("Redireccion remota sin cabecera Location."));
        return validateRemotePdfUri(currentUri.resolve(location).toString());
    }

    private void closeQuietly(InputStream input) {
        if (input == null) {
            return;
        }
        try {
            input.close();
        } catch (IOException ignored) {
            // Nothing to do while closing a rejected remote response body.
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookPdfResource loadPdfForReading(Long bookId, String authenticatedEmail) {
        Book book = getBook(bookId);
        if (book.getPdfPath() == null || book.getPdfPath().isBlank()) {
            throw new ResourceNotFoundException("Este libro no tiene un PDF disponible.");
        }

        User user = getUser(authenticatedEmail);
        boolean canRead = isPrivileged(user)
                || loanRepository.existsByUserIdAndBookIdAndStatus(user.getId(), book.getId(), LOAN_ACTIVE);
        if (!canRead) {
            throw new AccessDeniedException("Debes tener un prestamo activo para leer este libro.");
        }

        Resource resource = new FileSystemResource(Path.of(book.getPdfPath()));
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("PDF no encontrado en el servidor.");
        }

        return BookPdfResource.builder()
                .resource(resource)
                .filename(book.getPdfOriginalFilename() == null ? "book.pdf" : book.getPdfOriginalFilename())
                .build();
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + bookId));
    }

    private User getUser(String authenticatedEmail) {
        return userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private boolean isPrivileged(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole()) || "LIBRARIAN".equalsIgnoreCase(user.getRole());
    }

    private void validatePdfMetadata(String name, String contentType, long size) {
        String safeName = name == null ? "" : name.toLowerCase(Locale.ROOT);
        String safeContentType = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        boolean looksLikePdf = safeName.endsWith(".pdf") || safeContentType.contains("pdf");
        if (!looksLikePdf) {
            throw new BusinessRuleException("El recurso debe ser un PDF.");
        }
        long limit = maxPdfBytes > 0 ? maxPdfBytes : DEFAULT_MAX_PDF_BYTES;
        if (size > limit) {
            throw new BusinessRuleException("El PDF supera el tamano maximo permitido.");
        }
    }

    private String buildPdfFilename(Book book, String originalName) {
        String safeTitle = book.getTitle() == null ? "book" : book.getTitle().replaceAll("[^a-zA-Z0-9_-]", "_");
        if (safeTitle.length() > 40) {
            safeTitle = safeTitle.substring(0, 40);
        }
        String extension = originalName != null && originalName.toLowerCase(Locale.ROOT).endsWith(".pdf") ? ".pdf" : ".pdf";
        return safeTitle + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
    }

    private Path resolveStoragePath(String filename) {
        try {
            Path directory = Path.of(pdfDirectory).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            return directory.resolve(filename).normalize();
        } catch (IOException ex) {
            throw new BusinessRuleException("No se pudo preparar el almacenamiento de PDFs.");
        }
    }

    private void replaceBookPdf(Book book, Path target, String filename, String sourceUrl) {
        deletePreviousPdf(book);
        book.setPdfPath(target.toString());
        book.setPdfOriginalFilename(filename);
        book.setPdfSourceUrl(sourceUrl);
        bookRepository.save(book);
    }

    private void deletePreviousPdf(Book book) {
        if (book.getPdfPath() == null || book.getPdfPath().isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Path.of(book.getPdfPath()));
        } catch (IOException ignored) {
            // The DB is updated even if an old orphaned file cannot be removed.
        }
    }

    private void copyWithLimit(InputStream input, Path target, long limit) throws IOException {
        Path temp = Files.createTempFile(target.getParent(), "download-", ".pdf");
        long copied = 0;
        byte[] buffer = new byte[8192];
        try {
            int read;
            while ((read = input.read(buffer)) != -1) {
                copied += read;
                if (copied > limit) {
                    throw new BusinessRuleException("El PDF supera el tamano maximo permitido.");
                }
                Files.write(temp, java.util.Arrays.copyOf(buffer, read), java.nio.file.StandardOpenOption.APPEND);
            }
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private BookPdfResponse toResponse(Book book, String message) {
        return BookPdfResponse.builder()
                .bookId(book.getId())
                .message(message)
                .filename(book.getPdfOriginalFilename())
                .pdfUrl("/api/books/" + book.getId() + "/pdf")
                .build();
    }
}
