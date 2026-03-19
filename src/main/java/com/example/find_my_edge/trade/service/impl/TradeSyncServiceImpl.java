package com.example.find_my_edge.trade.service.impl;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.integrations.borkers.common.exception.BrokerNotAvailableException;
import com.example.find_my_edge.integrations.borkers.common.exception.TradeFetchFailedException;
import com.example.find_my_edge.integrations.borkers.common.exception.UserNotConnectedException;
import com.example.find_my_edge.trade.broker.BrokerTradeFetcher;
import com.example.find_my_edge.trade.broker.BrokerTradeFetcherFactory;
import com.example.find_my_edge.trade.dto.TradeSyncStatusResponse;
import com.example.find_my_edge.trade.entity.TradeSyncStatusEntity;
import com.example.find_my_edge.trade.enums.TradeFetchStatus;
import com.example.find_my_edge.trade.enums.TradeFetchType;
import com.example.find_my_edge.trade.mapper.TradeSyncStatusMapper;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.repository.TradeSyncStatusRespository;
import com.example.find_my_edge.trade.service.TradeService;
import com.example.find_my_edge.trade.service.TradeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeSyncServiceImpl implements TradeSyncService {

    private final TradeService tradeService;
    private final BrokerTradeFetcherFactory brokerTradeFetcherFactory;
    private final CurrentUserService currentUserService;
    private final TradeSyncStatusRespository syncStatusRepository;

    private final TradeSyncStatusMapper tradeSyncStatusMapper;

    private BrokerTradeFetcher getTradeFetcher(Broker broker) {
        BrokerTradeFetcher fetcher = brokerTradeFetcherFactory.get(broker);

        if (fetcher == null) {
            throw new BrokerNotAvailableException("Unsupported broker: " + broker.getName());
        }

        return fetcher;
    }

    private void executeSync(
            Broker broker,
            TradeFetchType type,
            SyncOperation operation
    ) {
        UUID userId = currentUserService.getUserId();

        TradeSyncStatusEntity entity =
                syncStatusRepository.findByUserIdAndBroker(userId, broker)
                                    .orElse(new TradeSyncStatusEntity());

        entity.setUserId(userId);
        entity.setBroker(broker);
        entity.setType(type);
        entity.setStatus(TradeFetchStatus.FETCHING);
        entity.setSyncStartedAt(Instant.now());

        syncStatusRepository.save(entity);

        try {
            List<Trade> trades = operation.fetch();

            tradeService.upsertTrades(trades);

            Instant now = Instant.now();

            entity.setStatus(TradeFetchStatus.COMPLETED);
            entity.setSyncEndedAt(now);
            entity.setLastFetchedAt(now);

        } catch (Exception ex) {
            entity.setStatus(TradeFetchStatus.FAILED);
            entity.setSyncEndedAt(Instant.now());

            throw new TradeFetchFailedException("Trade sync failed: " + broker.getName()); // rethrow so controller knows
        }

        syncStatusRepository.save(entity);
    }


    @Override
    public void fullSync(Broker broker) {
        BrokerTradeFetcher fetcher = getTradeFetcher(broker);

        executeSync(
                broker,
                TradeFetchType.FULL,
                fetcher::fetchAllTrades
        );
    }

    @Override
    public void incrementalSync(Broker broker) {
        BrokerTradeFetcher fetcher = getTradeFetcher(broker);

        executeSync(
                broker,
                TradeFetchType.INCREMENTAL,
                () -> fetcher.fetchIncrementalTrades(getLastFetchedAt(broker))
        );
    }

    @Override
    public void customSync(Broker broker, LocalDate fromDate, LocalDate toDate) {
        BrokerTradeFetcher fetcher = getTradeFetcher(broker);

        executeSync(
                broker,
                TradeFetchType.CUSTOM,
                () -> fetcher.fetchCustom(fromDate, toDate)
        );
    }

    @Override
    public Instant getLastFetchedAt(Broker broker) {
        UUID userId = currentUserService.getUserId();

        return syncStatusRepository.findByUserIdAndBroker(userId, broker)
                                   .map(TradeSyncStatusEntity::getLastFetchedAt)
                                   .orElse(null);
    }

    @Override
    public TradeSyncStatusResponse getTradeSyncStatus(Broker broker) {
        UUID userId = currentUserService.getUserId();


        TradeSyncStatusEntity statusEntity =
                syncStatusRepository.findByUserIdAndBroker(userId, broker)
                                    .orElseThrow(() -> new UserNotConnectedException(broker.toString().toLowerCase()));

        return tradeSyncStatusMapper.toResponse(statusEntity);
    }


    @FunctionalInterface
    interface SyncOperation {
        List<Trade> fetch();
    }
}