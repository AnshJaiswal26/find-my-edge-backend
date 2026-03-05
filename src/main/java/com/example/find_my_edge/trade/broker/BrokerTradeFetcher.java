package com.example.find_my_edge.trade.broker;

import com.example.find_my_edge.trade.model.Trade;

import java.time.LocalDate;
import java.util.List;

public interface BrokerTradeFetcher {

    String getName();

    List<Trade> fetchAllTrades();

    List<Trade> fetchIncrementalTrades();

    List<Trade> fetchCustom(LocalDate fromDate, LocalDate toDate);
}
