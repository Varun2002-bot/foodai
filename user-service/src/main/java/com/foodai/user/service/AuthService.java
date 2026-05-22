package com.foodai.user.service;

import com.foodai.user.dto.request.LoginRequest;
import com.foodai.user.dto.request.SignupRequest;
import com.foodai.user.dto.response.AuthResponse;
import com.foodai.user.dto.response.SignupResponse;

public interface AuthService {

    SignupResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
