package com.khanh.exercise_jwts.service;

import com.khanh.exercise_jwts.entity.Token;
import com.khanh.exercise_jwts.entity.User;
import com.khanh.exercise_jwts.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void revokeAll(User user) {
        tokenRepository.revokeAllByUser(user);
    }

    public void saveAccessToken(String jwt, User user) {
        Token token = Token.builder()
                .token(jwt)
                .user(user)
                .expired(false)
                .revoked(false)
                .type("ACCESS")
                .build();
        tokenRepository.save(token);
    }

    public void saveRefreshToken(String jwt, User user) {
        Token token = Token.builder()
                .token(jwt)
                .user(user)
                .expired(false)
                .revoked(false)
                .type("REFRESH")
                .build();
        tokenRepository.save(token);
    }
}
