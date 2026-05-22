package com.foodai.user.security;

import com.foodai.user.entity.Role;
import com.foodai.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateTokenShouldContainExpectedClaims() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "jwtSecret",
                "Zm9vZGFpLXN1cGVyLXNlY3JldC1rZXktZm9yLWhzMjU2LXRva2VuLXNpZ25pbmctMDEyMzQ1Njc4OQ=="
        );
        ReflectionTestUtils.setField(jwtService, "expirationMillis", 3_600_000L);

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Varun")
                .email("varun@example.com")
                .password("$2a$10$hashed")
                .role(Role.CUSTOMER)
                .build();

        String token = jwtService.generateToken(user);

        assertEquals("varun@example.com", jwtService.extractSubject(token));
        assertEquals("varun@example.com", jwtService.extractEmail(token));
        assertEquals("CUSTOMER", jwtService.extractRole(token));
        assertEquals(user.getId(), jwtService.extractUserId(token));
        assertTrue(jwtService.isTokenValid(token));
    }
}
