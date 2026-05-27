package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookAvailabilityResponse {
    private Long bookId;
    private long totalCopies;
    private long availableCopies;
    private long loanedCopies;
    private long reservedCopies;
    private long waitingReservations;
}
