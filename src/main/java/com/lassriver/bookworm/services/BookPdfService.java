package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.response.BookPdfResource;
import com.lassriver.bookworm.dtos.response.BookPdfResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BookPdfService {
    BookPdfResponse uploadPdf(Long bookId, MultipartFile file);

    BookPdfResponse downloadPdfFromUrl(Long bookId, String url);

    BookPdfResource loadPdfForReading(Long bookId, String authenticatedEmail);
}
