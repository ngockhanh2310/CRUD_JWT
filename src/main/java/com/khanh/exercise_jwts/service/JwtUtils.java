package com.khanh.exercise_jwts.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtUtils {

    private final Key signingKey;

    // 30 minutes for access token
    @Value("${jwt.expiration}")
    private long accessExpirationMillis;
    // 7 days for refresh token
    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    public JwtUtils(@Value("${jwt.signing.key}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(ensureBase64(secret));
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
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return true;
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

    private String ensureBase64(String secret) {
        // if already Base64, decoding will succeed. If not, encode as base64 by padding using hex approach is not here.
        // For simplicity, if not base64, we will base64-encode the original string bytes.
        try {
            Decoders.BASE64.decode(secret);
            return secret;
        } catch (Exception ex) {
            return io.jsonwebtoken.io.Encoders.BASE64.encode(secret.getBytes());
        }
    }
}
