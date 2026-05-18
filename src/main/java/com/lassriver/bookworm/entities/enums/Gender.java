package com.lassriver.bookworm.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    M,
    F,
    NR, // Usamos NR porque en Java los Enums no suelen llevar caracteres especiales
        // como "/"
    OTHER;

    @JsonCreator
    public static Gender fromJson(String value) {
        if (value == null || value.isBlank()) {
            return NR;
        }

        return switch (value.trim().toUpperCase()) {
            case "MALE", "M" -> M;
            case "FEMALE", "F" -> F;
            case "N_R", "N/R", "NR" -> NR;
            case "OTHER" -> OTHER;
            default -> throw new IllegalArgumentException("Genero invalido: " + value);
        };
    }
}
