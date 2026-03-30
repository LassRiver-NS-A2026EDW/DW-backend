package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.exceptions.EmailAlreadyExistsException;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // ¡El bean de Spring va en la implementación!
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        // 1. Validar si el correo ya existe (AC-1-5)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("El correo electrónico ya está registrado.");
        }

        // 2. Mapear el DTO a la Entidad
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // 3. Hashear la contraseña con Argon2id antes de guardar (AC-1-7)
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Asignar valores por defecto
        user.setRole("USER");
        user.setLanguage("es");

        // 5. Guardar en la base de datos
        User savedUser = userRepository.save(user);

        log.info("Simulando envío de correo de bienvenida a: {}", savedUser.getEmail());

        // 6. Retornar el DTO de respuesta
        return UserRegistrationResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .message("Usuario registrado exitosamente. Revisa tu correo electrónico.")
                .build();
    }
}