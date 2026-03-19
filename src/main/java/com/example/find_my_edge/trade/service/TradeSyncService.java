package com.example.find_my_edge.trade.service;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.trade.dto.TradeSyncStatusResponse;

import java.time.Instant;
import java.time.LocalDate;

public interface TradeSyncService {

    void fullSync(Broker broker);

    void incrementalSync(Broker broker);

    void customSync(Broker broker, LocalDate fromDate, LocalDate toDate);

    Instant getLastFetchedAt(Broker broker);

    TradeSyncStatusResponse getTradeSyncStatus(Broker broker);
}
