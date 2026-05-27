package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long bookId;
    private String bookTitle;
    private String status;
    private Integer requestedLoanDurationMinutes;
    private Integer queuePosition;
    private Long fulfilledLoanId;
    private LocalDateTime createdAt;
    private LocalDateTime fulfilledAt;
    private LocalDateTime cancelledAt;
}
