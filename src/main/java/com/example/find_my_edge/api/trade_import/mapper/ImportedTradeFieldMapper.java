package com.example.find_my_edge.api.trade_import.mapper;

import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.api.trade_import.dto.FieldDataRequestDto;
import com.example.find_my_edge.api.trade_import.dto.FieldDataResponseDto;
import com.example.find_my_edge.trade_import.entity.ImportedTradeFieldEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImportedTradeFieldMapper {

    private final JsonUtil jsonUtil;

    public ImportedTradeFieldEntity toEntity(FieldDataRequestDto dto) {
        return ImportedTradeFieldEntity.builder()
                                       .label(dto.getLabel())
                                       .type(dto.getType())
                                       .value(dto.getValue())
                                       .mappedColumn(dto.getMappedColumn())
                                       .mappedWith(dto.getMappedWith())
                                       .options(jsonUtil.toJson(dto.getOptions()))
                                       .build();
    }

    public FieldDataResponseDto toResponseDto(ImportedTradeFieldEntity field) {
        List<String> options = Collections.singletonList(jsonUtil.fromJson(
                field.getOptions(),
                String.class
        ));

        return FieldDataResponseDto.builder()
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
