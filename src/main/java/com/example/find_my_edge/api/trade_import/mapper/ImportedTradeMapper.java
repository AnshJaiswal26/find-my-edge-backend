package com.example.find_my_edge.api.trade_import.mapper;

import com.example.find_my_edge.api.trade_import.dto.ImportedTradeResponseDto;
import com.example.find_my_edge.trade_import.entity.ImportedTradeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportedTradeMapper {

    private final ImportedTradeFieldMapper fieldMapper;

    public ImportedTradeResponseDto toResponse(ImportedTradeEntity importedTradeEntity) {
        return new ImportedTradeResponseDto(
                importedTradeEntity.getId(),
                importedTradeEntity.getCreatedAt(),
                importedTradeEntity.getFields()
                                   .stream()
                                   .map(fieldMapper::toResponseDto)
                                   .toList()
        );
    }
}
