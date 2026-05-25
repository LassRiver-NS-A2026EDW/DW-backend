package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PdfDownloadRequest {

    @NotBlank(message = "La URL del PDF es obligatoria")
    @Size(max = 2000, message = "La URL del PDF no puede superar 2000 caracteres")
    private String url;
}
