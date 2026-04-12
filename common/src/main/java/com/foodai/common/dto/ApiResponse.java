package com.foodai.common.dto;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Request processed successfully", data);
    }

    public static ApiResponse<Void> successMessage(String message) {
        return success(message, null);
    }
}
