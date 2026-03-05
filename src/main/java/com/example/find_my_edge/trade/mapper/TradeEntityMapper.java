package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.trade.entity.TradeEntity;
import com.example.find_my_edge.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TradeEntityMapper {

    /* ---------------- MODEL → ENTITY ---------------- */

    public TradeEntity toEntity(Trade model) {
        if (model == null) return null;

        TradeEntity entity = new TradeEntity();

        entity.setId(model.getId()); // for update

        // Extract structured fields from values

        entity.setExternalId(model.getExternalId());

        entity.setDate(model.getDate());

        entity.setEntryTime(model.getEntryTime());

        entity.setExitTime(model.getExitTime());

        entity.setSymbol(model.getSymbol());

        entity.setDirection(model.getDirection());

        entity.setCharges(model.getCharges());

        entity.setEntryPrice(model.getEntryPrice());
        entity.setExitPrice(model.getExitPrice());

        entity.setQty(model.getQty());

        Map<String, Object> values =
                model.getValues() != null
                ? new HashMap<>(model.getValues())
                : new HashMap<>();

        // Keep full values JSON (including structured fields if you want)
        entity.setValues(values);

        return entity;
    }

    /* ---------------- ENTITY → MODEL ---------------- */

    public Trade toDomain(TradeEntity entity) {
        if (entity == null) return null;

        Map<String, Object> values =
                entity.getValues() != null
                ? new HashMap<>(entity.getValues())
                : new HashMap<>();

        return Trade.builder()
                    .id(entity.getId())
                    .externalId(entity.getExternalId())
                    .date(entity.getDate())
                    .entryTime(entity.getEntryTime())
                    .exitTime(entity.getExitTime())
                    .entryPrice(entity.getEntryPrice())
                    .exitPrice(entity.getExitPrice())
                    .qty(entity.getQty())
                    .charges(entity.getCharges())
                    .symbol(entity.getSymbol())
                    .direction(entity.getDirection())
                    .values(values)
                    .build();
    }
}