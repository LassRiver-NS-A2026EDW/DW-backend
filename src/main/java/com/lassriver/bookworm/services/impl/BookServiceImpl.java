package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.dtos.response.BookResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.ReviewRepository;
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

    private static final String REVIEW_VISIBLE = "VISIBLE";

    @Override
    public Page<BookResponse> getBooks(String title, String category, Pageable pageable) {
        Specification<Book> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return bookRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public BookResponse getBook(Long id) {
        return bookRepository.findById(id)
                .map(this::toResponse)
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
    public BookResponse deactivateBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));

        book.setStatus("INACTIVE");

        Book updatedBook = bookRepository.save(book);
        return toResponse(updatedBook);
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
        long reviewCount = reviewRepository.countByBookIdAndStatus(book.getId(), REVIEW_VISIBLE);
        Double rating = reviewRepository.averageRatingByBookIdAndStatus(book.getId(), REVIEW_VISIBLE);

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
                .createdAt(book.getCreatedAt())
                .build();
    }
}
