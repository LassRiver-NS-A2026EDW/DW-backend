package com.lassriver.bookworm.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

import com.lassriver.bookworm.entities.enums.Gender;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El formato del correo es inválido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotNull(message = "El género es obligatorio")
    private Gender gender;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    private LocalDate birthDate;

}