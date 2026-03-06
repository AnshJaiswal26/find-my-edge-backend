package com.example.find_my_edge.common.auth.service.impl;

import com.example.find_my_edge.common.auth.entity.RefreshToken;
import com.example.find_my_edge.common.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    private final long REFRESH_EXPIRY = 7 * 24 * 60 * 60 * 1000L;

    @Transactional
    public String createToken(UUID userId) {

        String token = UUID.randomUUID().toString();

        RefreshToken refresh = new RefreshToken();
        refresh.setToken(token);
        refresh.setUserId(userId);
        refresh.setCreatedAt(Instant.now());
        refresh.setExpiry(Instant.now().plusMillis(REFRESH_EXPIRY));

        repository.save(refresh);

        return token;
    }

    public RefreshToken verify(String token) {

        RefreshToken refresh =
                repository.findByToken(token)
                          .orElseThrow(() ->
                                               new RuntimeException("Invalid refresh token"));

        if (refresh.getExpiry().isBefore(Instant.now())) {
            repository.delete(refresh);
            throw new RuntimeException("Refresh token expired");
        }

        return refresh;
    }

    @Transactional
    public void delete(UUID userId) {
        repository.deleteByUserId(userId);
    }
}
