package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.LoginRequest;
import com.lassriver.bookworm.dtos.request.UserProfileUpdateRequest;
import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.dtos.response.LoginResponse;
import com.lassriver.bookworm.dtos.response.UserProfileResponse;
import com.lassriver.bookworm.dtos.response.UserRegistrationResponse;

public interface UserService {
    UserRegistrationResponse registerUser(UserRegistrationRequest request);

    LoginResponse login(LoginRequest request);

    UserProfileResponse getCurrentProfile(String authenticatedEmail);

    UserProfileResponse updateCurrentProfile(String authenticatedEmail, UserProfileUpdateRequest request);
}
