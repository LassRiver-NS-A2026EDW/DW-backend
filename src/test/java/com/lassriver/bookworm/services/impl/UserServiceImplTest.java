package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.LoginRequest;
import com.lassriver.bookworm.dtos.request.PasswordChangeRequest;
import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.LoginResponse;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.EmailAlreadyExistsException;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.security.JwtService;
import com.lassriver.bookworm.entities.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

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

    @Mock
    private AuthenticationManager authenticationManager; // Faltaba este mock

    @Mock
    private JwtService jwtService; // Faltaba este mock

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_HappyPath_ReturnsResponse() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Brian");
        request.setEmail("brian@test.com");
        request.setPassword("password123");
        request.setGender(Gender.F);
        request.setBirthDate(LocalDate.of(2000, 1, 1));

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setGender(request.getGender());
        savedUser.setBirthDate(request.getBirthDate());

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$argon2id$v=19$m=32768,t=2,p=1$hash");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserRegistrationResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("duplicado@test.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WhenBirthDateDoesNotMeetMinimumAge_ThrowsBusinessRuleException() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Young User");
        request.setEmail("young@test.com");
        request.setPassword("password123");
        request.setGender(Gender.F);
        request.setBirthDate(LocalDate.now().minusYears(12));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> userService.registerUser(request));

        assertTrue(exception.getMessage().contains("edad minima"));
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_HappyPath_ReturnsLoginResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("kevin@test.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("kevin@test.com");
        user.setRole("USER");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("token-falso-jwt");

        LoginResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("token-falso-jwt", response.getToken());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("kevin@test.com");
        request.setPassword("wrong-pass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        assertThrows(org.springframework.security.authentication.BadCredentialsException.class,
                () -> userService.login(request));
    }

    @Test
    void changeCurrentPassword_HappyPath_UpdatesPassword() {
        User user = new User();
        user.setEmail("kevin@test.com");
        user.setPassword("encoded-current");

        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("Password123!");
        request.setNewPassword("NewPassword123!");

        when(userRepository.findByEmail("kevin@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "encoded-current")).thenReturn(true);
        when(passwordEncoder.matches("NewPassword123!", "encoded-current")).thenReturn(false);
        when(passwordEncoder.encode("NewPassword123!")).thenReturn("encoded-new");

        userService.changeCurrentPassword("kevin@test.com", request);

        assertEquals("encoded-new", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changeCurrentPassword_WhenCurrentPasswordIsWrong_ThrowsBusinessRuleException() {
        User user = new User();
        user.setEmail("kevin@test.com");
        user.setPassword("encoded-current");

        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("NewPassword123!");

        when(userRepository.findByEmail("kevin@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-current")).thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> userService.changeCurrentPassword("kevin@test.com", request));
        verify(userRepository, never()).save(any(User.class));
    }
}
