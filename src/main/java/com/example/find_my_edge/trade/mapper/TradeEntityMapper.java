package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.trade.entity.TradeEntity;
import com.example.find_my_edge.trade.model.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.HashMap;
import java.util.Map;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TradeEntityMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tradeSetup", ignore = true)
    @Mapping(target = "values", expression = "java(copyMap(model.getValues()))")
    TradeEntity toEntity(Trade model);

    @Mapping(target = "values", expression = "java(copyMap(entity.getValues()))")
    Trade toModel(TradeEntity entity);

    default Map<String, Object> copyMap(Map<String, Object> source) {
        return source != null ? new HashMap<>(source) : new HashMap<>();
    }
}