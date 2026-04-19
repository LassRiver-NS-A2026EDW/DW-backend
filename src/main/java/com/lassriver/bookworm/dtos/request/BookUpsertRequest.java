package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookUpsertRequest {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título no puede superar 255 caracteres")
    private String title;

    @NotBlank(message = "El autor no puede estar vacío")
    @Size(max = 255, message = "El autor no puede superar 255 caracteres")
    private String author;

    @NotBlank(message = "El ISBN no puede estar vacío")
    @Size(max = 20, message = "El ISBN no puede superar 20 caracteres")
    private String isbn;

    @Size(max = 100, message = "La categoría no puede superar 100 caracteres")
    private String category;

    @Size(max = 10, message = "El idioma no puede superar 10 caracteres")
    private String language;

    @Size(max = 2000, message = "La URL de portada no puede superar 2000 caracteres")
    private String coverUrl;
}
