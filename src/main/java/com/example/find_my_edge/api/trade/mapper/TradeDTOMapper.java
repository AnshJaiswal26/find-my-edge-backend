package com.example.find_my_edge.api.trade.mapper;

import com.example.find_my_edge.api.trade.dto.TradeRequestDTO;
import com.example.find_my_edge.api.trade.dto.TradeResponseDTO;
import com.example.find_my_edge.domain.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TradeDTOMapper {

    public Trade toModel(TradeRequestDTO dto) {
        return Trade.builder()
                    .values(dto.getValues() != null
                            ? new HashMap<>(dto.getValues())
                            : new HashMap<>())
                    .build();
    }

    public TradeResponseDTO toDTO(Trade model) {
        TradeResponseDTO dto = new TradeResponseDTO();
        dto.setId(model.getId());
        dto.setValues(model.getValues());
        return dto;
    }
}