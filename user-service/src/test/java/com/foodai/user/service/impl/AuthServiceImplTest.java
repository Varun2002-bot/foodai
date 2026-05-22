package com.foodai.user.service.impl;

import com.foodai.common.exception.BusinessException;
import com.foodai.user.dto.request.LoginRequest;
import com.foodai.user.dto.response.AuthResponse;
import com.foodai.user.dto.request.SignupRequest;
import com.foodai.user.dto.response.SignupResponse;
import com.foodai.user.entity.Role;
import com.foodai.user.entity.User;
import com.foodai.user.repository.UserRepository;
import com.foodai.user.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void signupShouldCreateUserWithHashedPassword() {
        SignupRequest request = SignupRequest.builder()
                .name("Varun")
                .email("Varun@Example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail("varun@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(user.getCreatedAt());
            return user;
        });

        SignupResponse response = authService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("Varun", savedUser.getName());
        assertEquals("varun@example.com", savedUser.getEmail());
        assertEquals("$2a$10$hashed", savedUser.getPassword());
        assertNotEquals("password123", savedUser.getPassword());
        assertEquals(Role.CUSTOMER, response.getRole());
        assertEquals("varun@example.com", response.getEmail());
    }

    @Test
    void signupShouldRejectDuplicateEmail() {
        SignupRequest request = SignupRequest.builder()
                .name("Varun")
                .email("varun@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("varun@example.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.signup(request));

        assertEquals("Email is already registered: varun@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signupShouldRejectShortPassword() {
        SignupRequest request = SignupRequest.builder()
                .name("Varun")
                .email("varun@example.com")
                .password("short")
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.signup(request));

        assertTrue(exception.getMessage().contains("at least 8 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnJwtWhenCredentialsAreValid() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Varun")
                .email("varun@example.com")
                .password("$2a$10$hashed")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        LoginRequest request = LoginRequest.builder()
                .email("Varun@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail("varun@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("password123", "$2a$10$hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationMillis()).thenReturn(3_600_000L);

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("varun@example.com", response.getUser().getEmail());
    }

    @Test
    void loginShouldRejectInvalidCredentials() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Varun")
                .email("varun@example.com")
                .password("$2a$10$hashed")
                .role(Role.CUSTOMER)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email("varun@example.com")
                .password("wrong-password")
                .build();

        when(userRepository.findByEmail("varun@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "$2a$10$hashed")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
