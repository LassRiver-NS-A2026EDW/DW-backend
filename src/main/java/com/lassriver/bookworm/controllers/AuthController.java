package com.lassriver.bookworm.controllers;

import com.lassriver.bookworm.dtos.request.LoginRequest;
import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.LoginResponse;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;
import com.lassriver.bookworm.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // Endpoint de Registro (HU-F01-01)
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserRegistrationResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint de Login (HU-F01-02)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}