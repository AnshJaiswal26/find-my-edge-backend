package com.example.find_my_edge.api.dashboard.mapper;

import com.example.find_my_edge.analytics.model.TradeContextSplit;
import com.example.find_my_edge.api.dashboard.dto.TradeContextResponseDto;

public class TradeContextMapper {

    public static TradeContextResponseDto toResponse(TradeContextSplit split) {
        return new TradeContextResponseDto(
                split.getRaw(),
                split.getComputed(),
                split.getTradesOrder()
        );
    }
}