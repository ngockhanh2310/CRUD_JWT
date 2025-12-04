package com.khanh.exercise_jwts.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthenticateRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
