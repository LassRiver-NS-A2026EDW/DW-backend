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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookPdfServiceImpl implements BookPdfService {

    private static final String LOAN_ACTIVE = "ACTIVE";
    private static final long DEFAULT_MAX_PDF_BYTES = 50L * 1024L * 1024L;

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Value("${bookworm.storage.pdf-dir:uploads/pdfs}")
    private String pdfDirectory;

    @Value("${bookworm.storage.max-pdf-bytes:52428800}")
    private long maxPdfBytes;

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
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            throw new BusinessRuleException("URL invalida. Debe comenzar con http:// o https://.");
        }

        Book book = getBook(bookId);
        String filename = buildPdfFilename(book, cleanUrl);
        Path target = resolveStoragePath(filename);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(cleanUrl))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessRuleException("No se pudo descargar el PDF. Estado remoto: " + response.statusCode());
            }
            String contentType = response.headers().firstValue("Content-Type").orElse("");
            validatePdfMetadata(cleanUrl, contentType, response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(0L));

            try (InputStream input = response.body()) {
                copyWithLimit(input, target, maxPdfBytes > 0 ? maxPdfBytes : DEFAULT_MAX_PDF_BYTES);
            }
            replaceBookPdf(book, target, filename, cleanUrl);
        } catch (BusinessRuleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessRuleException("Error al descargar el PDF.");
        }

        return toResponse(book, "PDF descargado y guardado exitosamente.");
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
