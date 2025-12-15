package com.example.find_my_edge.core.backtest.mapper;

import com.example.find_my_edge.core.backtest.dto.TradeRecordsResponse;
import com.example.find_my_edge.core.backtest.entity.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeMapper {

    private final TradeFieldMapper fieldMapper;

    public TradeRecordsResponse toResponse(Trade trade) {
        return new TradeRecordsResponse(
                trade.getId(),
                trade.getCreatedAt(),
                trade.getFields()
                     .stream()
                     .map(fieldMapper::toResponse)
                     .toList()
        );
    }
}
