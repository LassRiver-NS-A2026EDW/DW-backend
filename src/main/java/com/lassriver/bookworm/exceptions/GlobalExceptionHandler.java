package com.lassriver.bookworm.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Manejo de Not Found (Del profe)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        ApiError body = new ApiError("NOT_FOUND", ex.getMessage(), "warning", Instant.now(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 2. Nuestra excepción de correo duplicado
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest req) {
        // Retornamos 409 Conflict o 400 Bad Request
        ApiError body = new ApiError("EMAIL_ALREADY_EXISTS", ex.getMessage(), "warning", Instant.now(),
                req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 3. Manejo de validaciones de DTOs (Del profe, ajustado con severity)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("code", "VALIDATION_ERROR");
        body.put("message", "Request validation failed");
        body.put("severity", "error"); //
        body.put("timestamp", Instant.now());
        body.put("path", req.getRequestURI());
        body.put("fields", fields);

        return ResponseEntity.badRequest().body(body);
    }

    // 4. Errores de formato en la petición (ej. valor de enum inválido)
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest req) {
        String msg = "Cuerpo de la petición mal formado o valores inválidos.";

        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException ife = (com.fasterxml.jackson.databind.exc.InvalidFormatException) ex
                    .getCause();
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                msg = String.format("Valor inválido: '%s' para el campo '%s'. Los valores permitidos son: %s",
                        ife.getValue(),
                        ife.getPath().get(ife.getPath().size() - 1).getFieldName(),
                        java.util.Arrays.toString(ife.getTargetType().getEnumConstants()));
            }
        }

        ApiError body = new ApiError("BAD_REQUEST", msg, "error",
                Instant.now(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 5. Errores no controlados 500 (Requisito HU-T02-02 AC-2)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest req) {
        ApiError body = new ApiError("INTERNAL_SERVER_ERROR", "Ocurrió un error inesperado en el servidor.", "error",
                Instant.now(), req.getRequestURI());
        // En la vida real, aquí meterías un log.error() para ver qué falló en consola
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // 5. Manejo de errores de credencials incorrectas
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex, HttpServletRequest req) {
        // Retornamos 401 Unauthorized con un mensaje genérico por seguridad
        ApiError body = new ApiError(
                "INVALID_CREDENTIALS",
                "Correo electrónico o contraseña incorrectos.",
                "warning",
                Instant.now(),
                req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}