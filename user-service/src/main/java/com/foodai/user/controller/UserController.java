package com.foodai.user.controller;

import com.foodai.common.dto.ApiResponse;
import com.foodai.common.utils.ResponseBuilder;
import com.foodai.user.dto.response.UserResponse;
import com.foodai.user.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        UserResponse response = UserResponse.builder()
                .id(authenticatedUser.id())
                .name(authenticatedUser.name())
                .email(authenticatedUser.email())
                .role(authenticatedUser.role())
                .createdAt(authenticatedUser.createdAt())
                .updatedAt(authenticatedUser.updatedAt())
                .build();

        return ResponseEntity.ok(ResponseBuilder.success("User profile fetched successfully", response));
    }
}
