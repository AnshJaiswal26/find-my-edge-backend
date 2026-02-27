package com.example.find_my_edge.domain.trade.mapper;

import com.example.find_my_edge.domain.trade.entity.TradeEntity;
import com.example.find_my_edge.domain.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TradeEntityMapper {

    /* ---------------- MODEL → ENTITY ---------------- */

    public TradeEntity toEntity(Trade model) {
        if (model == null) return null;

        TradeEntity entity = new TradeEntity();

        entity.setId(model.getId()); // important if updating

        entity.setValues(
                model.getValues() != null
                ? new HashMap<>(model.getValues()) // defensive copy
                : new HashMap<>()
        );

        return entity;
    }

    /* ---------------- ENTITY → MODEL ---------------- */

    public Trade toDomain(TradeEntity entity) {
        if (entity == null) return null;

        return Trade.builder()
                    .id(entity.getId())
                    .values(
                            entity.getValues() != null
                            ? new HashMap<>(entity.getValues()) // defensive copy
                            : new HashMap<>()
                    )
                    .build();
    }
}