package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookCopyResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String copyCode;
    private String status;
    private LocalDateTime createdAt;
}
