package com.example.find_my_edge.common.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    private String token;

    private UUID userId;

    private Instant expiry;

    private Instant createdAt;

}