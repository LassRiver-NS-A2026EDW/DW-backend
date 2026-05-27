package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.response.BookAvailabilityResponse;
import com.lassriver.bookworm.dtos.response.BookCopyResponse;

import java.util.List;

public interface BookCopyService {
    BookAvailabilityResponse getAvailability(Long bookId);

    BookCopyResponse createCopy(Long bookId);

    List<BookCopyResponse> getCopies(Long bookId);
}
