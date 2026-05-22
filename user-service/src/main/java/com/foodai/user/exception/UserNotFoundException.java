package com.foodai.user.exception;

import com.foodai.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    public UserNotFoundException(UUID userId) {
        super("User", userId);
    }
}
