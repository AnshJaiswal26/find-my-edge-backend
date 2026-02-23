package com.example.find_my_edge.core.trade.service.impl;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.core.trade.dto.TradeRequestDTO;
import com.example.find_my_edge.core.trade.dto.TradeResponseDTO;
import com.example.find_my_edge.core.trade.entity.TradeEntity;
import com.example.find_my_edge.core.trade.exception.TradeNotFoundException;
import com.example.find_my_edge.core.trade.mapper.TradeMapper;
import com.example.find_my_edge.core.trade.repository.TradeRepository;
import com.example.find_my_edge.core.trade.service.TradeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

    private final AuthService authService;
    private final TradeRepository tradeRepository;
    private final TradeMapper mapper;

    /* ---------------- CREATE ---------------- */

    @Override
    public TradeResponseDTO create(TradeRequestDTO request) {

        String userId = authService.getCurrentUserId();

        long now = Instant.now().toEpochMilli();

        TradeEntity entity = mapper.toEntity(request);

        entity.setId(UUID.randomUUID().toString());
        entity.setUserId(userId);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        TradeEntity saved = tradeRepository.save(entity);

        return mapper.toDTO(saved);
    }

    /* ---------------- UPDATE ---------------- */

    @Override
    public TradeResponseDTO update(String id, TradeRequestDTO request) {

        String userId = authService.getCurrentUserId();

        TradeEntity existing = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException("Trade not found"));

        TradeEntity updated = mapper.toEntity(request);

        updated.setId(existing.getId());
        updated.setUserId(userId);
        updated.setCreatedAt(existing.getCreatedAt()); // preserve
        updated.setUpdatedAt(Instant.now().toEpochMilli());

        return mapper.toDTO(tradeRepository.save(updated));
    }

    /* ---------------- GET BY ID ---------------- */

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public TradeResponseDTO getById(String id) {

        String userId = authService.getCurrentUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException("Trade not found"));

        return mapper.toDTO(entity);
    }

    /* ---------------- GET ALL ---------------- */

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public List<TradeResponseDTO> getAll() {

        String userId = authService.getCurrentUserId();

        return tradeRepository.findAllByUserId(userId)
                              .stream()
                              .map(mapper::toDTO)
                              .toList();
    }

    /* ---------------- DELETE ---------------- */

    @Override
    public void delete(String id) {

        String userId = authService.getCurrentUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException("Trade not found"));

        tradeRepository.delete(entity);
    }

}