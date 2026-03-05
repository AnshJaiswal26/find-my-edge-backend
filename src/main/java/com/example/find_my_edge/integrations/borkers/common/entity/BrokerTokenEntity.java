package com.example.find_my_edge.integrations.borkers.common.entity;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.integrations.borkers.common.enums.ConnectionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "broker"})
})
public class BrokerTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Broker broker;

    @Column(nullable = false, length = 2000)
    private String accessToken;

    private Instant expiry;

    private Instant lastFetchedAt;

    private Instant connectedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionStatus status;
}