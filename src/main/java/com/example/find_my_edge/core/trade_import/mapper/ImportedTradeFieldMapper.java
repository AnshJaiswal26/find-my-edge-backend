package com.example.find_my_edge.core.trade_import.mapper;

import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.core.trade_import.dto.FieldDataRequestDTO;
import com.example.find_my_edge.core.trade_import.dto.FieldDataResponseDTO;
import com.example.find_my_edge.core.trade_import.entity.ImportedTradeFieldEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImportedTradeFieldMapper {

    private final JsonUtil jsonUtil;

    public ImportedTradeFieldEntity toEntity(FieldDataRequestDTO dto) {
        return ImportedTradeFieldEntity.builder()
                                       .label(dto.getLabel())
                                       .type(dto.getType())
                                       .value(dto.getValue())
                                       .mappedColumn(dto.getMappedColumn())
                                       .mappedWith(dto.getMappedWith())
                                       .options(jsonUtil.toJson(dto.getOptions()))
                                       .build();
    }

    public FieldDataResponseDTO toResponseDTO(ImportedTradeFieldEntity field) {
        List<String> options = Collections.singletonList(jsonUtil.fromJson(
                field.getOptions(),
                String.class
        ));

        return FieldDataResponseDTO.builder()
                                   .id(field.getId())
                                   .label(field.getLabel())
                                   .type(field.getType())
                                   .value(field.getValue())
                                   .mappedWith(field.getMappedWith())
                                   .mappedColumn(field.getMappedColumn())
                                   .options(options)
                                   .build();
    }
}
