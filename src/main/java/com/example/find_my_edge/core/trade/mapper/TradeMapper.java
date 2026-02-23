package com.example.find_my_edge.core.trade.mapper;

import com.example.find_my_edge.core.trade.dto.TradeRequestDTO;
import com.example.find_my_edge.core.trade.dto.TradeResponseDTO;
import com.example.find_my_edge.core.trade.entity.TradeEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TradeMapper {

    /* ---------------- REQUEST → ENTITY ---------------- */

    public TradeEntity toEntity(TradeRequestDTO dto) {
        if (dto == null) return null;

        TradeEntity entity = new TradeEntity();

        entity.setValues(
                dto.getValues() != null
                ? new HashMap<>(dto.getValues()) // defensive copy
                : new HashMap<>()
        );

        return entity;
    }

    /* ---------------- ENTITY → RESPONSE ---------------- */

    public TradeResponseDTO toDTO(TradeEntity entity) {
        if (entity == null) return null;

        TradeResponseDTO dto = new TradeResponseDTO();

        dto.setId(entity.getId());
        dto.setValues(
                entity.getValues() != null
                ? new HashMap<>(entity.getValues()) // defensive copy
                : new HashMap<>()
        );

        return dto;
    }
}