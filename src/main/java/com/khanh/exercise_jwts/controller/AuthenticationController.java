package com.khanh.exercise_jwts.controller;

import com.khanh.exercise_jwts.dto.request.AuthenticateRequest;
import com.khanh.exercise_jwts.dto.request.RegisterRequest;
import com.khanh.exercise_jwts.dto.response.ApiResponse;
import com.khanh.exercise_jwts.dto.response.AuthenticateResponse;
import com.khanh.exercise_jwts.dto.response.RegisterResponse;
import com.khanh.exercise_jwts.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user and return tokens")
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ApiResponse.<RegisterResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(authenticationService.register(request))
                .build();
    }

    @Operation(summary = "Login and return access/refresh tokens")
    @PostMapping("/login")
    public ApiResponse<AuthenticateResponse> authenticate(
            @Valid @RequestBody AuthenticateRequest request
    ) {
        return ApiResponse.<AuthenticateResponse>builder()
                .success(true)
                .message("Logged in successfully")
                .data(authenticationService.authenticate(request))
                .build();
    }

    @Operation(summary = "Refresh access token with refresh token")
    @PostMapping("/refresh")
    public ApiResponse<AuthenticateResponse> refresh(
            @RequestParam("refresh_token") String refreshToken
    ) {
        return ApiResponse.<AuthenticateResponse>builder()
                .success(true)
                .message("Refreshed successfully")
                .data(authenticationService.refresh(refreshToken))
                .build();
    }

    @Operation(summary = "Logout: revoke all tokens of current user")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            Authentication authentication
    ) {
        authenticationService.logout(authentication.getName());
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Logged out successfully")
                .build();
    }
}
