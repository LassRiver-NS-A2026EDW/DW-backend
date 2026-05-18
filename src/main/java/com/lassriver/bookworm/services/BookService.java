package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.dtos.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookResponse> getBooks(String search, String title, String category, String language, String status, Pageable pageable);

    BookResponse getBook(Long id);

    BookResponse createBook(BookUpsertRequest request);

    BookResponse updateBook(Long id, BookUpsertRequest request);

    BookResponse updateBookStatus(Long id, String status);
}
