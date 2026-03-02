package com.example.find_my_edge.integrations.borkers.dhan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Entity
@Data
public class DhanTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId; // your app user

    @Column(nullable = false, length = 2000)
    private String accessToken;

    private Instant lastFetchedAt;

    private Instant expiry;
}