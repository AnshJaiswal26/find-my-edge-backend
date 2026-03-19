package com.example.find_my_edge.trade.broker;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.trade.model.Trade;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface BrokerTradeFetcher {

    Broker getName();

    List<Trade> fetchAllTrades();

    List<Trade> fetchIncrementalTrades(Instant lastFetchedAt);

    List<Trade> fetchCustom(LocalDate fromDate, LocalDate toDate);
}
