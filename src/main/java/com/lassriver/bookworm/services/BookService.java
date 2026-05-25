package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.dtos.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookResponse> getBooks(String title, String category, Pageable pageable);

    Page<BookResponse> getBooks(String search, String title, String category, String language, String status, Pageable pageable, String authenticatedEmail);

    BookResponse getBook(Long id);

    BookResponse getBook(Long id, String authenticatedEmail);

    BookResponse createBook(BookUpsertRequest request);

    BookResponse updateBook(Long id, BookUpsertRequest request);

    BookResponse deactivateBook(Long id);
}
