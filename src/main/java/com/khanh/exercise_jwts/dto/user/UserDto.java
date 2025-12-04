package com.khanh.exercise_jwts.dto.user;

import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String fullName,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}
