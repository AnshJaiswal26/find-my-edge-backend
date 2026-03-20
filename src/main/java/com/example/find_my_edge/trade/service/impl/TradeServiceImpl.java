package com.example.find_my_edge.trade.service.impl;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.trade.entity.TradeEntity;
import com.example.find_my_edge.trade.exception.TradeIdNullException;
import com.example.find_my_edge.trade.exception.TradeNotFoundException;
import com.example.find_my_edge.trade.mapper.TradeEntityMapper;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.model.TradeBundle;
import com.example.find_my_edge.trade.repository.TradeRepository;
import com.example.find_my_edge.trade.service.TradeService;

import com.example.find_my_edge.trade_setup.entity.TradeSetupEntity;
import com.example.find_my_edge.trade_setup.service.TradeSetupService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

    private final CurrentUserService currentUserService;
    private final TradeRepository tradeRepository;
    private final TradeEntityMapper mapper;

    private final WorkspaceService workspaceService;

    private final TradeSetupService tradeSetupService;


    private static final Map<String, BiConsumer<TradeEntity, Object>> STATIC_FIELD_UPDATERS = Map.of(
            "date", (t, v) -> t.setDate(((Number) v).longValue()),
            "entryTime", (t, v) -> t.setEntryTime(((Number) v).intValue()),
            "exitTime", (t, v) -> t.setExitTime(((Number) v).intValue()),
            "symbol", (t, v) -> t.setSymbol((String) v),
            "direction", (t, v) -> t.setDirection((String) v),
            "charges", (t, v) -> t.setCharges(((Number) v).doubleValue()),
            "entryPrice", (t, v) -> t.setEntryPrice(((Number) v).doubleValue()),
            "exitPrice", (t, v) -> t.setExitPrice(((Number) v).doubleValue()),
            "qty", (t, v) -> t.setQty(((Number) v).intValue())
    );


    /* ---------------- CREATE ---------------- */
    @Override
    public Trade create(Trade trade) {

        if (trade.getId() == null) {
            throw new TradeIdNullException("Trade must have an id");
        }
        UUID userId = currentUserService.getUserId();

        long now = Instant.now().toEpochMilli();

        TradeEntity entity = mapper.toEntity(trade);

        if (trade.getTradeSetupId() != null) {
            TradeSetupEntity tradeSetup =
                    tradeSetupService.getOwnedReferenceOrThrow(trade.getTradeSetupId());
            entity.setTradeSetup(tradeSetup);
        }

        entity.setId(trade.getId());
        entity.setUserId(userId);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        TradeEntity saved = tradeRepository.save(entity);

        return mapper.toModel(saved);
    }

    @Override
    public void createAll(List<Trade> trades) {

        UUID userId = currentUserService.getUserId();
        long now = Instant.now().toEpochMilli();

        List<TradeEntity> entityList = new ArrayList<>();

        for (Trade trade : trades) {
            TradeEntity entity = mapper.toEntity(trade);

            if (trade.getTradeSetupId() != null) {
                TradeSetupEntity tradeSetup =
                        tradeSetupService.getOwnedReferenceOrThrow(trade.getTradeSetupId());
                entity.setTradeSetup(tradeSetup);
            }

            entity.setId(UUID.randomUUID().toString());
            entity.setUserId(userId);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            entityList.add(entity);
        }

        tradeRepository.saveAll(entityList);
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public Trade update(String id, Trade trade) {

        UUID userId = currentUserService.getUserId();

        TradeEntity existing = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        TradeEntity updated = mapper.toEntity(trade);

        updated.setId(existing.getId());
        updated.setUserId(userId);
        updated.setCreatedAt(existing.getCreatedAt()); // preserve
        updated.setUpdatedAt(Instant.now().toEpochMilli());

        TradeEntity saved = tradeRepository.save(updated);

        return mapper.toModel(saved);
    }

    @Override
    public Trade updateValue(String tradeId, String field, Object value) {

        UUID userId = currentUserService.getUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(tradeId, userId)
                .orElseThrow(() -> new TradeNotFoundException(tradeId));

        BiConsumer<TradeEntity, Object> updater = STATIC_FIELD_UPDATERS.get(field);

        if (updater != null) {
            updater.accept(entity, value);
        } else {
            entity.getValues().put(field, value);
        }

        entity.setUpdatedAt(Instant.now().toEpochMilli());

        TradeEntity saved = tradeRepository.save(entity);

        return mapper.toModel(saved);
    }

    @Override
    public Trade updateTradeSetup(String tradeId, String setupId) {

        UUID userId = currentUserService.getUserId();

        TradeEntity trade = tradeRepository
                .findByIdAndUserId(tradeId, userId)
                .orElseThrow(() -> new TradeNotFoundException(tradeId));

        // Handle REMOVE case
        if (setupId == null) {
            trade.setTradeSetup(null);
        } else {
            TradeSetupEntity setup =
                    tradeSetupService.getOwnedReferenceOrThrow(setupId);

            trade.setTradeSetup(setup);
        }

        trade.setUpdatedAt(Instant.now().toEpochMilli());

        TradeEntity saved = tradeRepository.save(trade);

        return mapper.toModel(saved);
    }

    /* ---------------- GET BY ID ---------------- */
    @Transactional(readOnly = true)
    @Override
    public Trade getById(String id) {

        UUID userId = currentUserService.getUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        return mapper.toModel(entity);
    }

    /* ---------------- GET ALL ---------------- */
    @Transactional(readOnly = true)
    @Override
    public TradeBundle getTradeBundle() {

        List<Trade> trades = getAll();

        List<String> tradeOrder = new ArrayList<>();
        Map<String, Trade> tradesById = new HashMap<>();

        for (Trade trade : trades) {
            tradeOrder.add(trade.getId());
            tradesById.put(trade.getId(), trade);
        }

        return new TradeBundle(tradeOrder, tradesById);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Trade> getAll() {

        UUID userId = currentUserService.getUserId();

        List<TradeEntity> tradeEntities =
                tradeRepository.findAllByUserIdOrderByDateAscEntryTimeAsc(userId);

        return tradeEntities.stream().map(mapper::toModel).toList();
    }

    /* ---------------- DELETE ---------------- */
    @Override
    public void delete(String id) {

        UUID userId = currentUserService.getUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        tradeRepository.delete(entity);
        workspaceService.removeTradeReferences(id);
    }

    @Override
    public void deleteAll() {
        UUID userId = currentUserService.getUserId();
        tradeRepository.deleteAllByUserId(userId);
    }

    @Override
    public void upsertTrades(List<Trade> trades) {

        UUID userId = currentUserService.getUserId();
        long now = Instant.now().toEpochMilli();

        List<TradeEntity> entities = new ArrayList<>();

        for (Trade trade : trades) {

            String externalId = trade.getExternalId();

            TradeEntity existing = tradeRepository
                    .findByUserIdAndExternalId(userId, externalId)
                    .orElse(null);

            TradeEntity entity = mapper.toEntity(trade);

            if (existing != null) {
                // existing trade
                entity.setId(existing.getId());
                entity.setCreatedAt(existing.getCreatedAt());
            } else {
                // new trade
                String id = UUID.randomUUID().toString();

                trade.setId(id);
                entity.setId(id);
                entity.setCreatedAt(now);
            }

            entity.setUserId(userId);
            entity.setExternalId(externalId); // important
            entity.setUpdatedAt(now);

            entities.add(entity);
        }

        tradeRepository.saveAll(entities);
    }

    @Override
    public void removeSchemaReferences(String id) {

        UUID userId = currentUserService.getUserId();

        List<TradeEntity> trades = tradeRepository.findAllByUserId(userId);

        long now = Instant.now().toEpochMilli();

        for (TradeEntity trade : trades) {
            Map<String, Object> values = trade.getValues();

            if (values != null && values.remove(id) != null) {
                trade.setUpdatedAt(now);
            }
        }

        tradeRepository.saveAll(trades);
    }
}