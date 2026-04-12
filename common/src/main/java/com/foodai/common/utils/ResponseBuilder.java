package com.foodai.common.utils;

import com.foodai.common.constants.AppConstants;
import com.foodai.common.dto.ApiResponse;

public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(AppConstants.DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    public static ApiResponse<Void> successMessage(String message) {
        return ApiResponse.successMessage(message);
    }
}
