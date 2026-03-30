package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.exceptions.EmailAlreadyExistsException;
import com.lassriver.bookworm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_HappyPath_ReturnsResponse() {
        // Arrange: Preparamos los datos
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Brian");
        request.setEmail("brian@test.com");
        request.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());

        // Simulamos que el correo NO existe en la BD
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // Simulamos el hash de Argon2id
        when(passwordEncoder.encode(anyString())).thenReturn("$argon2id$v=19$m=32768,t=2,p=1$hashsimulado");
        // Simulamos el guardado en BD
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act: Ejecutamos el método
        UserRegistrationResponse response = userService.registerUser(request);

        // Assert: Verificamos los resultados (AC-1-1 y AC-1-7 del .MD)
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Brian", response.getName());

        // Verificamos que el encoder se llamó exactamente una vez (clave para la
        // seguridad)
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("duplicado@test.com");

        // Simulamos que el correo SÍ existe
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert (AC-1-5 del .MD)
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.registerUser(request);
        });

        // Verificamos que NUNCA se intentó guardar en la base de datos
        verify(userRepository, never()).save(any(User.class));
    }
}