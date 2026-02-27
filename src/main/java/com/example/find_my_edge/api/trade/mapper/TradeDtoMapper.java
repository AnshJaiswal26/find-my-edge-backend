package com.example.find_my_edge.api.trade.mapper;

import com.example.find_my_edge.api.trade.dto.TradeRequestDto;
import com.example.find_my_edge.api.trade.dto.TradeResponseDto;
import com.example.find_my_edge.domain.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TradeDtoMapper {

    public Trade toDomain(TradeRequestDto dto) {
        return Trade.builder()
                    .values(dto.getValues() != null
                            ? new HashMap<>(dto.getValues())
                            : new HashMap<>())
                    .build();
    }

    public TradeResponseDto toResponse(Trade model) {
        TradeResponseDto dto = new TradeResponseDto();
        dto.setId(model.getId());
        dto.setValues(model.getValues());
        return dto;
    }
}