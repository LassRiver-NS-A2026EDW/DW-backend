package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoanRenewalResponse {
    private Long id;
    private Long loanId;
    private LocalDateTime previousDueDate;
    private LocalDateTime newDueDate;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
}
