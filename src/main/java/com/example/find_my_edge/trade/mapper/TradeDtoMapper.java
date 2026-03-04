package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.trade.dto.TradeDto;
import com.example.find_my_edge.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TradeDtoMapper {

    public Trade toModel(TradeDto dto) {
        return Trade.builder()
                    .id(dto.getId())
                    .externalId(dto.getExternalId())
                    .date(dto.getDate())
                    .entryTime(dto.getEntryTime())
                    .exitTime(dto.getExitTime())
                    .entryPrice(dto.getEntryPrice())
                    .exitPrice(dto.getExitPrice())
                    .qty(dto.getQty())
                    .charges(dto.getCharges())
                    .direction(dto.getDirection())
                    .symbol(dto.getSymbol())
                    .values(dto.getValues() != null
                            ? new HashMap<>(dto.getValues())
                            : new HashMap<>())
                    .build();
    }

    public TradeDto toResponse(Trade model) {
        TradeDto dto = new TradeDto();
        dto.setId(model.getId());
        dto.setExternalId(model.getExternalId());
        dto.setDate(model.getDate());
        dto.setEntryTime(model.getEntryTime());
        dto.setExitTime(model.getExitTime());
        dto.setEntryPrice(model.getEntryPrice());
        dto.setExitPrice(model.getExitPrice());
        dto.setQty(model.getQty());
        dto.setCharges(model.getCharges());
        dto.setDirection(model.getDirection());
        dto.setSymbol(model.getSymbol());
        dto.setValues(model.getValues());
        return dto;
    }
}