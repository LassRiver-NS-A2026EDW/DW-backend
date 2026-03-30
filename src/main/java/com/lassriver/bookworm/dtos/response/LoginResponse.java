package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long id;
    private String name;
    private String role; // Retornamos el perfil mínimo (HU-F01-02 AC-3)
}