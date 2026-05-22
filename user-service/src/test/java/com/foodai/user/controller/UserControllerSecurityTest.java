package com.foodai.user.controller;

import com.foodai.user.config.SecurityConfig;
import com.foodai.user.entity.Role;
import com.foodai.user.entity.User;
import com.foodai.user.repository.UserRepository;
import com.foodai.user.security.JwtAuthenticationEntryPoint;
import com.foodai.user.security.JwtAuthenticationFilter;
import com.foodai.user.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void meShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void meShouldReturnProfileWhenTokenIsValid() throws Exception {
        String token = "valid-jwt-token";
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Varun")
                .email("varun@example.com")
                .password("$2a$10$hashed")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractSubject(token)).thenReturn("varun@example.com");
        when(userRepository.findByEmail("varun@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/me")
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("varun@example.com"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }
}
