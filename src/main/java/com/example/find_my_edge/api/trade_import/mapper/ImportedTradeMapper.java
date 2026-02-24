package com.example.find_my_edge.api.trade_import.mapper;

import com.example.find_my_edge.api.trade_import.dto.ImportedTradeResponseDTO;
import com.example.find_my_edge.trade_import.entity.ImportedTradeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportedTradeMapper {

    private final ImportedTradeFieldMapper fieldMapper;

    public ImportedTradeResponseDTO toResponseDTO(ImportedTradeEntity importedTradeEntity) {
        return new ImportedTradeResponseDTO(
                importedTradeEntity.getId(),
                importedTradeEntity.getCreatedAt(),
                importedTradeEntity.getFields()
                                   .stream()
                                   .map(fieldMapper::toResponseDTO)
                                   .toList()
        );
    }
}
