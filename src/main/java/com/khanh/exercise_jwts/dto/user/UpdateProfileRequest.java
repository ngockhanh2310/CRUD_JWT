package com.khanh.exercise_jwts.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateProfileRequest(
        @Email String email,
        String fullName,
        @Size(min = 6, max = 100) String newPassword
) {
}
