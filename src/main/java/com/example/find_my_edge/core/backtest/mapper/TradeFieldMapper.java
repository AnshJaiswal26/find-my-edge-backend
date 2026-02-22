package com.example.find_my_edge.core.backtest.mapper;

import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.dto.FieldDataResponse;
import com.example.find_my_edge.core.backtest.entity.TradeField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TradeFieldMapper {

    public TradeField toEntity(FieldDataRequest dto) {
        return TradeField.builder()
                         .label(dto.getLabel())
                         .type(dto.getType())
                         .value(dto.getValue())
                         .mappedColumn(dto.getMappedColumn())
                         .mappedWith(dto.getMappedWith())
                         .options(JsonUtil.toJSON(dto.getOptions()))
                         .build();
    }

    public FieldDataResponse toResponse(TradeField field) {
        List<String> options = JsonUtil.fromJson(
                field.getOptions(),
                new TypeReference<List<String>>() {
                }
        );

        return FieldDataResponse.builder()
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
