package com.foodai.user.security;

import com.foodai.user.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        String name,
        String email,
        Role role,
        Instant createdAt,
        Instant updatedAt
) {
}
