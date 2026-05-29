package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.dtos.response.BookFacetsResponse;
import com.lassriver.bookworm.dtos.response.BookResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.BookCopy;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import com.lassriver.bookworm.entities.enums.ReservationStatus;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookCopyRepository;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReservationRepository;
import com.lassriver.bookworm.repositories.ReviewRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.BookService;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;

    private static final String REVIEW_VISIBLE = "VISIBLE";
    private static final int LOAN_COOLDOWN_HOURS = 24;
    private static final List<BookCopyStatus> COUNTED_COPY_STATUSES = List.of(
            BookCopyStatus.AVAILABLE,
            BookCopyStatus.LOANED,
            BookCopyStatus.RESERVED);

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(String search, String title, String category, String language, String status, Pageable pageable) {
        return getBooks(search, title, category, language, status, null, pageable, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(String search, String title, String category, String language, String status, String availability, Pageable pageable, String authenticatedEmail) {
        Specification<Book> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String term = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), term),
                        cb.like(cb.lower(root.get("author")), term),
                        cb.like(cb.lower(root.get("isbn")), term),
                        cb.like(cb.lower(root.get("description")), term)));
            }

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }

            if (language != null && !language.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("language")), language.toLowerCase()));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), normalizeStatus(status)));
            }

            if (availability != null && !availability.isBlank() && !"all".equalsIgnoreCase(availability)) {
                Subquery<Long> availableCopyQuery = query.subquery(Long.class);
                Root<BookCopy> copyRoot = availableCopyQuery.from(BookCopy.class);
                availableCopyQuery.select(copyRoot.get("id"))
                        .where(
                                cb.equal(copyRoot.get("book").get("id"), root.get("id")),
                                cb.equal(copyRoot.get("status"), BookCopyStatus.AVAILABLE));
                Predicate activeBook = cb.equal(cb.upper(root.get("status")), "ACTIVE");
                Predicate hasAvailableCopy = cb.exists(availableCopyQuery);

                if ("available".equalsIgnoreCase(availability)) {
                    predicates.add(cb.and(activeBook, hasAvailableCopy));
                } else if ("unavailable".equalsIgnoreCase(availability)) {
                    predicates.add(cb.or(cb.not(activeBook), cb.not(hasAvailableCopy)));
                } else {
                    throw new BusinessRuleException("Filtro de disponibilidad invalido: " + availability);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Long userId = resolveUserId(authenticatedEmail);
        return bookRepository.findAll(spec, pageable).map(book -> toResponse(book, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        return getBook(id, null);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id, String authenticatedEmail) {
        Long userId = resolveUserId(authenticatedEmail);
        return bookRepository.findById(id)
                .map(book -> toResponse(book, userId))
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BookFacetsResponse getBookFacets() {
        return BookFacetsResponse.builder()
                .categories(bookRepository.findDistinctCategories())
                .languages(bookRepository.findDistinctLanguages())
                .build();
    }

    @Override
    @Transactional
    public BookResponse createBook(BookUpsertRequest request) {
        Book book = new Book();
        mapRequestToEntity(request, book);

        Book savedBook = bookRepository.save(book);
        ensureInitialCopy(savedBook);
        return toResponse(savedBook);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookUpsertRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));

        mapRequestToEntity(request, book);

        Book updatedBook = bookRepository.save(book);
        return toResponse(updatedBook);
    }

    @Override
    @Transactional
    public BookResponse updateBookStatus(Long id, String status) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));

        book.setStatus(normalizeStatus(status));

        Book updatedBook = bookRepository.save(book);
        if ("ACTIVE".equals(updatedBook.getStatus())) {
            ensureInitialCopy(updatedBook);
        }
        return toResponse(updatedBook);
    }

    private void ensureInitialCopy(Book book) {
        if (!"ACTIVE".equalsIgnoreCase(book.getStatus())
                || bookCopyRepository.countByBookIdAndStatusIn(book.getId(), COUNTED_COPY_STATUSES) > 0) {
            return;
        }
        long nextCopyNumber = bookCopyRepository.countByBookId(book.getId()) + 1;
        bookCopyRepository.save(BookCopy.builder()
                .book(book)
                .copyCode("BOOK-" + book.getId() + "-COPY-" + nextCopyNumber)
                .status(BookCopyStatus.AVAILABLE)
                .build());
    }

    private String normalizeStatus(String status) {
        String normalized = status == null || status.isBlank() ? "INACTIVE" : status.trim().toUpperCase();
        if (!"ACTIVE".equals(normalized) && !"INACTIVE".equals(normalized)) {
            throw new BusinessRuleException("Estado de libro invalido: " + status);
        }
        return normalized;
    }

    private void mapRequestToEntity(BookUpsertRequest request, Book book) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setLanguage(request.getLanguage());
        book.setCoverUrl(request.getCoverUrl());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setPages(request.getPages());
        book.setDescription(request.getDescription());
    }

    private BookResponse toResponse(Book book) {
        return toResponse(book, null);
    }

    private BookResponse toResponse(Book book, Long userId) {
        long reviewCount = reviewRepository.countByBookIdAndStatus(book.getId(), REVIEW_VISIBLE);
        Double rating = reviewRepository.averageRatingByBookIdAndStatus(book.getId(), REVIEW_VISIBLE);
        boolean hasPdf = book.getPdfPath() != null && !book.getPdfPath().isBlank();
        boolean reservedByMe = userId != null
                && loanRepository.existsByUserIdAndBookIdAndStatus(userId, book.getId(), LoanStatus.ACTIVE);
        LocalDateTime loanCooldownUntil = getLoanCooldownUntil(userId, book.getId());
        long totalCopies = bookCopyRepository.countByBookIdAndStatusIn(book.getId(), COUNTED_COPY_STATUSES);
        long availableCopies = bookCopyRepository.countByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE);
        long waitingReservations = reservationRepository.countByBookIdAndStatus(book.getId(), ReservationStatus.WAITING);

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .language(book.getLanguage())
                .status(book.getStatus())
                .coverUrl(book.getCoverUrl())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .pages(book.getPages())
                .description(book.getDescription())
                .rating(rating == null ? 0.0 : Math.round(rating * 10.0) / 10.0)
                .reviewCount(reviewCount)
                .hasPdf(hasPdf)
                .pdfUrl(hasPdf ? "/api/books/" + book.getId() + "/pdf" : null)
                .reservedByMe(reservedByMe)
                .loanCooldownUntil(loanCooldownUntil)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .waitingReservations(waitingReservations)
                .createdAt(book.getCreatedAt())
                .build();
    }

    private LocalDateTime getLoanCooldownUntil(Long userId, Long bookId) {
        if (userId == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return loanRepository.findFirstByUserIdAndBookIdAndStatusAndReturnedAtAfterOrderByReturnedAtDesc(
                        userId,
                        bookId,
                        LoanStatus.RETURNED,
                        now.minusHours(LOAN_COOLDOWN_HOURS))
                .map(Loan::getReturnedAt)
                .map(returnedAt -> returnedAt.plusHours(LOAN_COOLDOWN_HOURS))
                .filter(cooldownUntil -> cooldownUntil.isAfter(now))
                .orElse(null);
    }

    private Long resolveUserId(String authenticatedEmail) {
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            return null;
        }
        return userRepository.findByEmail(authenticatedEmail)
                .map(user -> user.getId())
                .orElse(null);
    }
}
