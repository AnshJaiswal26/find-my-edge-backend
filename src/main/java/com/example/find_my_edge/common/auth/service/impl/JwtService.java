package com.example.find_my_edge.common.auth.service.impl;

import com.example.find_my_edge.common.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long ACCESS_EXPIRY = 1000 * 60 * 15L;

    public JwtService() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            this.secretKey = keyGenerator.generateKey();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate secret key");
        }
    }


    public String generateAccessToken(User user) {

        return Jwts.builder()
                   .subject(user.getId())
                   .claim("email", user.getEmail())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRY))
                   .signWith(secretKey)
                   .compact();
    }

    public String extractUserId(String token) {

        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

    public boolean isValid(String token) {

        try {
            Claims claims = extractClaims(token);
            return !isExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}