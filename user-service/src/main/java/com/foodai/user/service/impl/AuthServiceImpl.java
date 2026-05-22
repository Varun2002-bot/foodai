package com.foodai.user.service.impl;

import com.foodai.common.exception.BusinessException;
import com.foodai.user.dto.request.LoginRequest;
import com.foodai.user.dto.request.SignupRequest;
import com.foodai.user.dto.response.AuthResponse;
import com.foodai.user.dto.response.SignupResponse;
import com.foodai.user.dto.response.UserResponse;
import com.foodai.user.entity.User;
import com.foodai.user.exception.DuplicateEmailException;
import com.foodai.user.exception.InvalidCredentialsException;
import com.foodai.user.security.JwtService;
import com.foodai.user.repository.UserRepository;
import com.foodai.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public SignupResponse signup(SignupRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        validatePasswordLength(request.getPassword());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMillis())
                .user(toUserResponse(user))
                .build();
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException("Email is required");
        }
        return email.trim().toLowerCase();
    }

    private void validatePasswordLength(String password) {
        if (!StringUtils.hasText(password) || password.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException("Password must be at least 8 characters long");
        }
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
