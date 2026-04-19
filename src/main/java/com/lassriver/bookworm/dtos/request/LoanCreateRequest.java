package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanCreateRequest {

    @NotNull(message = "El id del libro es obligatorio")
    private Long bookId;
}
