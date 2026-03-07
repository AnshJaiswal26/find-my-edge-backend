package com.example.find_my_edge.common.auth.service.impl;

import com.example.find_my_edge.common.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private final long ACCESS_EXPIRY = 1000 * 60 * 15L;

//    public SecretKey getKey() { // for base64 encoded secret keys
//        byte[] decode = Base64.getDecoder().decode(secret);
//        return Keys.hmacShaKeyFor(decode);
//    }

    public SecretKey getKey() {  // for normal secret keys
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateAccessToken(User user) {

        return Jwts.builder()
                   .subject(user.getId().toString())
                   .claim("email", user.getEmail())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRY))
                   .signWith(getKey())
                   .compact();
    }

    public UUID extractUserId(String token) {

        String subject = Jwts.parser()
                             .verifyWith(getKey())
                             .build()
                             .parseSignedClaims(token)
                             .getPayload()
                             .getSubject();

        return UUID.fromString(subject);
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
                   .verifyWith(getKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}