package com.foodai.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.user.dto.request.LoginRequest;
import com.foodai.user.dto.response.AuthResponse;
import com.foodai.user.dto.request.SignupRequest;
import com.foodai.user.dto.response.SignupResponse;
import com.foodai.user.entity.Role;
import com.foodai.user.security.JwtAuthenticationFilter;
import com.foodai.user.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void signupShouldReturnCreated() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .name("Varun")
                .email("varun@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();

        SignupResponse response = SignupResponse.builder()
                .id(UUID.randomUUID())
                .name("Varun")
                .email("varun@example.com")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(authService.signup(any(SignupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("varun@example.com"));
    }

    @Test
    void loginShouldReturnToken() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("varun@example.com")
                .password("password123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken("jwt-token")
                .tokenType("Bearer")
                .expiresIn(3_600_000L)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"));
    }
}
