package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.LoginRequest;
import com.lassriver.bookworm.dtos.request.UserProfileUpdateRequest;
import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.LoginResponse;
import com.lassriver.bookworm.dtos.response.UserProfileResponse;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.EmailAlreadyExistsException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.security.JwtService;
import com.lassriver.bookworm.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Para validar login
    private final JwtService jwtService; // Para generar el token

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("El correo electrónico ya está registrado.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Argon2id (HU-T01-03)
        user.setRole("USER");
        user.setLanguage("es");
        user.setGender(request.getGender());
        user.setBirthDate(request.getBirthDate());

        User savedUser = userRepository.save(user);
        log.info("Usuario registrado: {}", savedUser.getEmail());

        return UserRegistrationResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .gender(savedUser.getGender())
                .birthDate(savedUser.getBirthDate())
                .message("Usuario registrado exitosamente.")
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. Autenticar: Spring Security usa el Argon2id para comparar (HU-F01-02)
        // Si falla, lanza una excepción que capturará nuestro GlobalExceptionHandler
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // 2. Si llegamos aquí, las credenciales son válidas
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 3. Generar el JWT (HU-T01-01)
        String token = jwtService.generateToken(user);

        log.info("Login exitoso para el usuario: {}", user.getEmail());

        // 4. Devolver perfil mínimo e ID (HU-F01-02 AC-3)
        return LoginResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentProfile(String authenticatedEmail) {
        return toProfileResponse(getUserByEmail(authenticatedEmail));
    }

    @Override
    @Transactional
    public UserProfileResponse updateCurrentProfile(String authenticatedEmail, UserProfileUpdateRequest request) {
        User user = getUserByEmail(authenticatedEmail);
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new BusinessRuleException("El cambio de correo requiere reautenticacion y no esta disponible en esta fase.");
        }

        user.setName(request.getName());
        return toProfileResponse(userRepository.save(user));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .build();
    }
}
