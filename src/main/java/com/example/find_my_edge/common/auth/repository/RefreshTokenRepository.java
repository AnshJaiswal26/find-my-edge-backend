package com.example.find_my_edge.common.auth.repository;

import com.example.find_my_edge.common.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);


    void deleteByUserId(UUID userId);

    void deleteByToken(String token);
}