package com.example.find_my_edge.trade.service.impl;

import com.example.find_my_edge.integrations.borkers.common.exception.BrokerNotAvailableException;
import com.example.find_my_edge.trade.broker.BrokerTradeFetcher;
import com.example.find_my_edge.trade.broker.BrokerTradeFetcherFactory;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.service.TradeService;
import com.example.find_my_edge.trade.service.TradeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeSyncServiceImpl implements TradeSyncService {

    private final TradeService tradeService;

    private final BrokerTradeFetcherFactory brokerTradeFetcherFactory;

    private BrokerTradeFetcher getTradeFetcher(String broker) {
        BrokerTradeFetcher brokerTradeFetcher =
                brokerTradeFetcherFactory.get(broker);

        if (brokerTradeFetcher == null) {
            throw new BrokerNotAvailableException("Unsupported broker: " + broker);
        }

        return brokerTradeFetcher;
    }

    @Override
    public void fullSync(String broker) {
        BrokerTradeFetcher brokerTradeFetcher = getTradeFetcher(broker);

        List<Trade> trades = brokerTradeFetcher.fetchAllTrades();

        tradeService.upsertTrades(trades);
    }

    @Override
    public void incrementalSync(String broker) {
        BrokerTradeFetcher brokerTradeFetcher = getTradeFetcher(broker);

        List<Trade> trades = brokerTradeFetcher.fetchIncrementalTrades();

        tradeService.upsertTrades(trades);
    }

    @Override
    public void customSync(String broker, LocalDate fromDate, LocalDate toDate) {
        BrokerTradeFetcher brokerTradeFetcher = getTradeFetcher(broker);

        List<Trade> trades = brokerTradeFetcher.fetchCustom(fromDate, toDate);

        tradeService.upsertTrades(trades);
    }
}
