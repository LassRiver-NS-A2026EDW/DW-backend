package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;

public interface UserService {
    // Definimos el contrato: entra un request, sale un response
    UserRegistrationResponse registerUser(UserRegistrationRequest request);
}