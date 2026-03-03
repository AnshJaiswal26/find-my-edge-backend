package com.example.find_my_edge.integrations.borkers.dhan.entity;

import jakarta.persistence.*;
import lombok.Data;


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