package com.talentflow.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenTtl;

    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration}") long accessExpiration,
            @Value("${app.jwt.refresh-token-expiration}") long refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessExpiration;
        this.refreshTokenTtl = refreshExpiration;
    }

    public String generateAccessToken(UUID userId, UUID companyId, String email, String role, List<String> permissions) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("company_id", companyId.toString())
                .claim("email", email)
                .claim("role", role)
                .claim("permissions", permissions != null ? permissions : List.of())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRawRefreshToken() {
        byte[] bytes = new byte[32];
        new java.security.SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public long getAccessTokenExpiration() { return accessTokenExpiration; }
    public long getRefreshTokenTtl() { return refreshTokenTtl; }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            validateToken(token);
            return false;
        } catch (ExpiredJwtException e) { return true;
        } catch (JwtException e) { return true; }
    }
}
