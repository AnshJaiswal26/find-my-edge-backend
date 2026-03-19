package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.trade.dto.TradeSyncStatusResponse;
import com.example.find_my_edge.trade.entity.TradeSyncStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TradeSyncStatusMapper {
    TradeSyncStatusResponse toResponse(TradeSyncStatusEntity entity);
}