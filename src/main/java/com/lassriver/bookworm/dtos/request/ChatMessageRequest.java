package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatMessageRequest {

    @NotBlank(message = "El rol del mensaje es obligatorio")
    @Size(max = 20, message = "El rol no puede superar 20 caracteres")
    private String role;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Size(max = 4000, message = "El contenido del mensaje no puede superar 4000 caracteres")
    private String content;
}
