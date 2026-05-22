package com.foodai.user.controller;


import com.foodai.common.dto.ApiResponse;
import com.foodai.common.utils.ResponseBuilder;
import com.foodai.user.dto.request.UpdateUserRoleRequest;
import com.foodai.user.dto.response.UserSummaryResponse;
import com.foodai.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> listUsers() {
        List<UserSummaryResponse> users = adminService.listUsers();
        return ResponseEntity.ok(ResponseBuilder.success("Users fetched successfully", users));
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> updateUserRole(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        UserSummaryResponse updated = adminService.updateUserRole(userId, request);
        return ResponseEntity.ok(ResponseBuilder.success("User role updated successfully", updated));
    }
}
