package com.lassriver.bookworm.dtos.response;

import com.lassriver.bookworm.entities.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Gender gender;
    private LocalDate birthDate;
}
