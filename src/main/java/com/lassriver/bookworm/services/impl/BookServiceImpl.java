package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.dtos.response.BookResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReviewRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.BookService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    private static final String REVIEW_VISIBLE = "VISIBLE";
    private static final String LOAN_ACTIVE = "ACTIVE";

    @Override
    public Page<BookResponse> getBooks(String search, String title, String category, String language, String status, Pageable pageable) {
        return getBooks(search, title, category, language, status, pageable, null);
    }

    @Override
    public Page<BookResponse> getBooks(String search, String title, String category, String language, String status, Pageable pageable, String authenticatedEmail) {
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Long userId = resolveUserId(authenticatedEmail);
        return bookRepository.findAll(spec, pageable).map(book -> toResponse(book, userId));
    }

    @Override
    public BookResponse getBook(Long id) {
        return getBook(id, null);
    }

    @Override
    public BookResponse getBook(Long id, String authenticatedEmail) {
        Long userId = resolveUserId(authenticatedEmail);
        return bookRepository.findById(id)
                .map(book -> toResponse(book, userId))
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

    @Override
    public BookResponse createBook(BookUpsertRequest request) {
        Book book = new Book();
        mapRequestToEntity(request, book);

        Book savedBook = bookRepository.save(book);
        return toResponse(savedBook);
    }

    @Override
    public BookResponse updateBook(Long id, BookUpsertRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));

        mapRequestToEntity(request, book);

        Book updatedBook = bookRepository.save(book);
        return toResponse(updatedBook);
    }

    @Override
    public BookResponse updateBookStatus(Long id, String status) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));

        book.setStatus(normalizeStatus(status));

        Book updatedBook = bookRepository.save(book);
        return toResponse(updatedBook);
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
                && loanRepository.existsByUserIdAndBookIdAndStatus(userId, book.getId(), LOAN_ACTIVE);

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
                .createdAt(book.getCreatedAt())
                .build();
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
