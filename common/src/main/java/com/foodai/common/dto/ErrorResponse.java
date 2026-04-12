package com.foodai.common.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        boolean success,
        String message,
        int status,
        String path,
        Instant timestamp,
        Map<String, String> validationErrors
) {

    public static ErrorResponse of(String message, int status, String path) {
        return new ErrorResponse(false, message, status, path, Instant.now(), Map.of());
    }

    public static ErrorResponse of(String message, int status, String path, Map<String, String> validationErrors) {
        return new ErrorResponse(false, message, status, path, Instant.now(), validationErrors);
    }
}
