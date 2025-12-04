package com.khanh.exercise_jwts.dto.response;

import lombok.Builder;

@Builder
public record AuthenticateResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public static AuthenticateResponse of(String access, String refresh) {
        return new AuthenticateResponse(access, refresh, "Bearer");
    }
}
