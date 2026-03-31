package com.lassriver.bookworm.dtos.response;

import java.time.LocalDate;
import com.lassriver.bookworm.entities.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationResponse {
    private Long id;
    private String name;
    private String email;
    private String message;
    private Gender gender;
    private LocalDate birthDate;
}