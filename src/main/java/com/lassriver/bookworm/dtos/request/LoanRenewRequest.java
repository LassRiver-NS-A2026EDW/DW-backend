package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRenewRequest {

    @NotNull(message = "La duracion de la renovacion es obligatoria")
    @Min(value = 5, message = "La duracion minima de la renovacion es 5 minutos")
    @Max(value = 10080, message = "La duracion maxima de la renovacion es 10080 minutos")
    private Integer durationMinutes;
}
