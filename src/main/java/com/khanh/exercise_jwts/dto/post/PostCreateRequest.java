package com.khanh.exercise_jwts.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PostCreateRequest(
        @NotBlank @Size(min = 3, max = 255) String title,
        @NotBlank String content
) {
}
