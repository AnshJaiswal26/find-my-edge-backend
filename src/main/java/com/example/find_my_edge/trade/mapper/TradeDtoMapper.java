package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.trade.dto.TradeDto;
import com.example.find_my_edge.trade.model.Trade;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.Map;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TradeDtoMapper {

    /* ---------------- DTO → MODEL ---------------- */
    @Mapping(target = "values", expression = "java(copyMap(dto.getValues()))")
    Trade toModel(TradeDto dto);

    /* ---------------- MODEL → DTO ---------------- */
    @Mapping(target = "values", expression = "java(copyMap(model.getValues()))")
    TradeDto toResponse(Trade model);

    /* ---------------- COMMON ---------------- */
    default Map<String, Object> copyMap(Map<String, Object> source) {
        return source != null ? new HashMap<>(source) : new HashMap<>();
    }
}