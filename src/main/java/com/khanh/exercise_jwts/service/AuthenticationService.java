package com.khanh.exercise_jwts.service;

import com.khanh.exercise_jwts.dto.request.AuthenticateRequest;
import com.khanh.exercise_jwts.dto.request.RegisterRequest;
import com.khanh.exercise_jwts.dto.response.AuthenticateResponse;
import com.khanh.exercise_jwts.dto.response.RegisterResponse;
import com.khanh.exercise_jwts.entity.User;
import com.khanh.exercise_jwts.enums.Role;
import com.khanh.exercise_jwts.mapper.AuthMapper;
import com.khanh.exercise_jwts.repository.TokenRepository;
import com.khanh.exercise_jwts.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthMapper authMapper;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        var user = authMapper.toUserRegister(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(Role.USER));
        return authMapper.toRegisterResponse(userRepository.save(user));
    }

    @Transactional
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userRepository.findByUsername(request.username()).orElseThrow();

        return issueNewTokens(user);
    }

    @Transactional
    public AuthenticateResponse refresh(String refreshToken) {
        if (jwtUtils.validateToken(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String username = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow();

        // Check token in DB is not revoked/expired
        tokenRepository.findByToken(refreshToken)
                .filter(t -> !t.isRevoked() && !t.isExpired())
                .orElseThrow(() -> new IllegalArgumentException("Refresh token is revoked or not found"));

        Map<String, Object> claims = buildRolesClaims(user);
        String newAccess = jwtUtils.generateAccessToken(user.getUsername(), claims);

        // Save new access token; keep refresh token as-is
        tokenService.saveAccessToken(newAccess, user);

        return AuthenticateResponse.of(newAccess, refreshToken);
    }

    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        tokenService.revokeAll(user);
    }

    // Helpers to avoid duplicated code
    private Map<String, Object> buildRolesClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        return claims;
    }

    private AuthenticateResponse issueNewTokens(User user) {
        Map<String, Object> claims = buildRolesClaims(user);
        String access = jwtUtils.generateAccessToken(user.getUsername(), claims);
        String refresh = jwtUtils.generateRefreshToken(user.getUsername());

        // Revoke all previous tokens for this user and save new ones
        tokenService.revokeAll(user);
        tokenService.saveAccessToken(access, user);
        tokenService.saveRefreshToken(refresh, user);

        return AuthenticateResponse.of(access, refresh);
    }
}
