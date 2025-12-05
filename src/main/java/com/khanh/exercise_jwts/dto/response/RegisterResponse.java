package com.khanh.exercise_jwts.dto.response;

import lombok.Builder;

import java.util.Set;

@Builder
public record RegisterResponse(
        String username,
        String email,
        String fullName,
        Set<String> roles
) {
}
