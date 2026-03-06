package com.example.find_my_edge.trade.service.impl;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.integrations.borkers.dhan.service.DhanOAuthService;
import com.example.find_my_edge.integrations.borkers.dhan.service.DhanTradeService;
import com.example.find_my_edge.trade.entity.TradeEntity;
import com.example.find_my_edge.trade.exception.TradeIdNullException;
import com.example.find_my_edge.trade.exception.TradeNotFoundException;
import com.example.find_my_edge.trade.mapper.ProcessedTradeMapper;
import com.example.find_my_edge.trade.mapper.TradeEntityMapper;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.repository.TradeRepository;
import com.example.find_my_edge.trade.service.TradeService;

import com.example.find_my_edge.workspace.service.WorkspaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

    private final CurrentUserService currentUserService;
    private final TradeRepository tradeRepository;
    private final TradeEntityMapper mapper;
    private final ProcessedTradeMapper processedTradeMapper;

    private final WorkspaceService workspaceService;

    private final DhanTradeService dhanTradeService;

    private final DhanOAuthService dhanOAuthService;


    /* ---------------- CREATE ---------------- */

    @Override
    public Trade create(Trade trade) {

        if (trade.getId() == null) {
            throw new TradeIdNullException("Trade must have an id");
        }

        UUID userId = currentUserService.getUserId();
        long now = Instant.now().toEpochMilli();

        TradeEntity entity = mapper.toEntity(trade);

        entity.setId(trade.getId());
        entity.setUserId(userId);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        TradeEntity saved = tradeRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public void createAll(List<Trade> trades) {

        UUID userId = currentUserService.getUserId();
        long now = Instant.now().toEpochMilli();

        List<TradeEntity> entityList = new ArrayList<>();

        for (Trade trade : trades) {
            TradeEntity entity = mapper.toEntity(trade);
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

        return mapper.toDomain(saved);
    }

    /* ---------------- GET BY ID ---------------- */

    @Transactional
    @Override
    public Trade getById(String id) {

        UUID userId = currentUserService.getUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        return mapper.toDomain(entity);
    }

    /* ---------------- GET ALL ---------------- */

    @Transactional
    @Override
    public List<Trade> getAll() {

        UUID userId = currentUserService.getUserId();

        return tradeRepository.findAllByUserIdOrderByDateAscEntryTimeAsc(userId)
                              .stream()
                              .map(mapper::toDomain)
                              .toList();
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

        tradeRepository.deleteByUserId(userId);
    }

    @Override
    public void upsertTrades(List<Trade> trades) {

        UUID userId = currentUserService.getUserId();
        long now = Instant.now().toEpochMilli();

        List<TradeEntity> entities = new ArrayList<>();

        for (Trade trade : trades) {

            String externalId = trade.getExternalId(); // YOUR LINE

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
}