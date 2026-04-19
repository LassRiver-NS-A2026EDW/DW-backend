package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewCreateRequest {

    @NotNull(message = "El id del libro es obligatorio")
    private Long bookId;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 2000, message = "El comentario no puede superar 2000 caracteres")
    private String comment;
}
