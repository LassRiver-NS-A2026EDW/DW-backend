package com.lassriver.bookworm.exceptions;

import lombok.Getter;
import java.time.Instant;

@Getter
public class ApiError {
    private String code;
    private String message;
    private String severity; // Añadido para cumplir con AC-21-1
    private Instant timestamp;
    private String path;

    public ApiError(String code, String message, String severity, Instant timestamp, String path) {
        this.code = code;
        this.message = message;
        this.severity = severity;
        this.timestamp = timestamp;
        this.path = path;
    }
}