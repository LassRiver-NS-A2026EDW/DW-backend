package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.response.BookAvailabilityResponse;
import com.lassriver.bookworm.dtos.response.BookCopyResponse;

import java.util.List;

public interface BookCopyService {
    BookAvailabilityResponse getAvailability(Long bookId);

    BookCopyResponse createCopy(Long bookId);

    BookCopyResponse retireCopy(Long bookId, Long copyId);

    List<BookCopyResponse> getCopies(Long bookId);
}
