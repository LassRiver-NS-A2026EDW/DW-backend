package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.LoginRequest;
import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.LoginResponse;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;

public interface UserService {
    // Registro de usuario (HU-F01-01)
    UserRegistrationResponse registerUser(UserRegistrationRequest request);
    
    // Inicio de sesión (HU-F01-02)
    LoginResponse login(LoginRequest request);
}