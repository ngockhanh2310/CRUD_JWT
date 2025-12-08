package com.khanh.exercise_jwts.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtUtils {

    private final Key signingKey;

    // 30 minutes for an access token
    @Value("${jwt.expiration}")
    private long accessExpirationMillis;
    // 7 days for a refresh token
    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    public JwtUtils(@Value("${jwt.signing.key}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, accessExpirationMillis);
    }

    public String generateRefreshToken(String subject) {
        return buildToken(subject, Map.of("typ", "refresh"), refreshExpirationMillis);
    }

    private String buildToken(String subject, Map<String, Object> claims, long ttlMillis) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("khanh")
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ttlMillis)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);
            Object typ = claims.get("typ");
            return typ != null && "refresh".equals(typ.toString());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
