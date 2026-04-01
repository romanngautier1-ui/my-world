package com.app.myworld.security;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.app.myworld.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs; // ex: 43200000 = 12h

    public String generateToken(UserDetails userDetails) {
        var builder = Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .claim("roles", userDetails.getAuthorities());

        if (userDetails instanceof User user) {
            builder.claim("userId", user.getId());
            builder.claim("username", user.getUsername());
            builder.claim("tokenVersion", user.getTokenVersion());
        }

        return builder
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public Integer extractTokenVersion(String token) {
        return extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
        );
    }

    private Key getSigningKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set property 'jwt.secret' (env: JWT_SECRET).");
        }

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret is too short for HS256. Provide at least 32 bytes (env: JWT_SECRET).");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
