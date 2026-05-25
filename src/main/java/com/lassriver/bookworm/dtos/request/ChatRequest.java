package com.lassriver.bookworm.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRequest {

    private Long bookId;

    @Size(max = 6000, message = "El texto seleccionado no puede superar 6000 caracteres")
    private String selectedText;

    @Size(max = 6000, message = "El contexto no puede superar 6000 caracteres")
    private String context;

    @NotBlank(message = "La pregunta es obligatoria")
    @Size(max = 2000, message = "La pregunta no puede superar 2000 caracteres")
    private String question;

    @Valid
    private List<ChatMessageRequest> history = new ArrayList<>();
}
