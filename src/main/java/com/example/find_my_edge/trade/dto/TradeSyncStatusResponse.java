package com.example.find_my_edge.trade.dto;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.trade.enums.TradeFetchStatus;
import com.example.find_my_edge.trade.enums.TradeFetchType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TradeSyncStatusResponse {
    private Broker broker;

    private TradeFetchStatus status;

    private TradeFetchType type;

    private Instant syncStartedAt;

    private Instant syncEndedAt;

    private Instant lastFetchedAt;
}