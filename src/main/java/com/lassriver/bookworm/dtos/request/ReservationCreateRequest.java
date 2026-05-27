package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationCreateRequest {

    @NotNull(message = "El id del libro es obligatorio")
    private Long bookId;

    @NotNull(message = "La duracion solicitada del prestamo es obligatoria")
    @Min(value = 5, message = "La duracion minima del prestamo es 5 minutos")
    @Max(value = 10080, message = "La duracion maxima del prestamo es 10080 minutos")
    private Integer requestedLoanDurationMinutes;
}
