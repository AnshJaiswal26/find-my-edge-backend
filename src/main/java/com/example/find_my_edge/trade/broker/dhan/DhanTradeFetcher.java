package com.example.find_my_edge.trade.broker.dhan;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.integrations.borkers.common.exception.NoTradesFoundException;
import com.example.find_my_edge.integrations.borkers.common.model.ProcessedTrade;
import com.example.find_my_edge.integrations.borkers.dhan.service.DhanTradeService;
import com.example.find_my_edge.trade.broker.BrokerTradeFetcher;
import com.example.find_my_edge.trade.mapper.ProcessedTradeMapper;
import com.example.find_my_edge.trade.model.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DhanTradeFetcher implements BrokerTradeFetcher {

    private final DhanTradeService dhanTradeService;

    private final ProcessedTradeMapper processedTradeMapper;

    @Override
    public Broker getName() {
        return Broker.DHAN;
    }

    private List<ProcessedTrade> fetchAllPages(LocalDate fromDate, LocalDate toDate) {

        List<ProcessedTrade> allTrades = new ArrayList<>();
        int page = 0;

        while (true) {
            try {
                List<ProcessedTrade> trades =
                        dhanTradeService.getTrades(fromDate, toDate, page);

                if (trades == null || trades.isEmpty()) break;

                allTrades.addAll(trades);
                page++;

            } catch (NoTradesFoundException e) {
                break; // no more pages
            }
        }

        return allTrades;
    }


    @Override
    public List<Trade> fetchAllTrades() {

        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.now();

        List<ProcessedTrade> raw = fetchAllPages(fromDate, toDate);

        return mapToTrades(raw);
    }

    @Override
    public List<Trade> fetchIncrementalTrades(Instant lastFetchedAt) {

        ZoneId zone = ZoneId.systemDefault();

        LocalDateTime fromDateTime = (lastFetchedAt != null)
                                     ? LocalDateTime.ofInstant(lastFetchedAt, zone).minusDays(1)
                                     : LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDate fromDate = fromDateTime.toLocalDate();
        LocalDate toDate = LocalDate.now();

        List<ProcessedTrade> raw = fetchAllPages(fromDate, toDate);

        return mapToTrades(raw);
    }

    @Override
    public List<Trade> fetchCustom(LocalDate fromDate, LocalDate toDate) {

        List<ProcessedTrade> raw = fetchAllPages(fromDate, toDate);

        return mapToTrades(raw);
    }


    private List<Trade> mapToTrades(List<ProcessedTrade> trades) {
        return trades.stream()
                     .map(processedTradeMapper::mapToTrade)
                     .toList();
    }
}
