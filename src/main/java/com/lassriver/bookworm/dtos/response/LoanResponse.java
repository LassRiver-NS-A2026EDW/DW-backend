package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoanResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDateTime loanDate;
    private LocalDateTime returnedAt;
    private String status;
    private LocalDateTime createdAt;
}
