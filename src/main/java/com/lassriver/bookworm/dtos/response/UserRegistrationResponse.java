package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationResponse {
    private Long id;
    private String name;
    private String email;
    private String message;
}