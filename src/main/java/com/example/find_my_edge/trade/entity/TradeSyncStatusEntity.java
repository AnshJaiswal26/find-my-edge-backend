package com.example.find_my_edge.trade.entity;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.trade.enums.TradeFetchStatus;
import com.example.find_my_edge.trade.enums.TradeFetchType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class TradeSyncStatusEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private Broker broker;

    @Enumerated(EnumType.STRING)
    private TradeFetchStatus status;

    @Enumerated(EnumType.STRING)
    private TradeFetchType type;

    private Instant syncStartedAt;

    private Instant syncEndedAt;

    private Instant lastFetchedAt;
}
