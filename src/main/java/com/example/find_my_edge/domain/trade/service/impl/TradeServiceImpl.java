package com.example.find_my_edge.domain.trade.service.impl;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.domain.trade.entity.TradeEntity;
import com.example.find_my_edge.domain.trade.exception.TradeNotFoundException;
import com.example.find_my_edge.domain.trade.mapper.TradeEntityMapper;
import com.example.find_my_edge.domain.trade.model.Trade;
import com.example.find_my_edge.domain.trade.repository.TradeRepository;
import com.example.find_my_edge.domain.trade.service.TradeService;

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

    private final AuthService authService;
    private final TradeRepository tradeRepository;
    private final TradeEntityMapper mapper;

    /* ---------------- CREATE ---------------- */

    @Override
    public Trade create(Trade trade) {

        String userId = authService.getCurrentUserId();
        long now = Instant.now().toEpochMilli();

        TradeEntity entity = mapper.toEntity(trade);

        entity.setId(UUID.randomUUID().toString());
        entity.setUserId(userId);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        TradeEntity saved = tradeRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public void createAll(List<Trade> trades) {

        String userId = authService.getCurrentUserId();
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

        String userId = authService.getCurrentUserId();

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

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public Trade getById(String id) {

        String userId = authService.getCurrentUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        return mapper.toDomain(entity);
    }

    /* ---------------- GET ALL ---------------- */

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public List<Trade> getAll() {

        String userId = authService.getCurrentUserId();

        return tradeRepository.findAllByUserId(userId)
                              .stream()
                              .map(mapper::toDomain)
                              .toList();
    }

    /* ---------------- DELETE ---------------- */

    @Override
    public void delete(String id) {

        String userId = authService.getCurrentUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        tradeRepository.delete(entity);
    }


    @Override
    public void deleteAll() {

        String userId = authService.getCurrentUserId();

        tradeRepository.deleteByUserId(userId);
    }
}