package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookPdfResponse {
    private Long bookId;
    private String message;
    private String filename;
    private String pdfUrl;
}
