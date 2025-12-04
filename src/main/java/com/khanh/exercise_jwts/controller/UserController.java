package com.khanh.exercise_jwts.controller;

import com.khanh.exercise_jwts.dto.response.ApiResponse;
import com.khanh.exercise_jwts.dto.user.UpdateProfileRequest;
import com.khanh.exercise_jwts.dto.user.UserDto;
import com.khanh.exercise_jwts.enums.Role;
import com.khanh.exercise_jwts.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user's profile")
    @GetMapping("/me")
    public ApiResponse<UserDto> me() {
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("Get current user's profile success")
                .data(userService.getProfile())
                .build();
    }

    @Operation(summary = "Update current user's profile")
    @PutMapping("/me")
    public ApiResponse<UserDto> update(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("Update current user's profile success")
                .data(userService.updateProfile(request))
                .build();
    }

    // Admin endpoints
    @Operation(summary = "List all users (ADMIN)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserDto>> list() {
        return ApiResponse.<List<UserDto>>builder()
                .success(true)
                .message("List all users success")
                .data(userService.listUsers())
                .build();
    }

    @Operation(summary = "Add role to user (ADMIN)")
    @PostMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDto> addRole(@PathVariable Long id, @PathVariable String role) {
        Role roles = Role.valueOf("ROLE_" + role.toUpperCase());
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("Add role to user success")
                .data(userService.addRole(id, roles))
                .build();
    }

    @Operation(summary = "Remove role from user (ADMIN)")
    @DeleteMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDto> removeRole(@PathVariable Long id, @PathVariable String role) {
        Role roles = Role.valueOf("ROLE_" + role.toUpperCase());
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("Remove role from user success")
                .data(userService.removeRole(id, roles))
                .build();
    }

    @Operation(summary = "Delete user (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Delete user success")
                .build();
    }
}
