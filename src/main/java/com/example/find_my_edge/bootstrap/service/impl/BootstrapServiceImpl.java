package com.example.find_my_edge.bootstrap.service.impl;

import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.bootstrap.dto.BootstrapResponse;
import com.example.find_my_edge.bootstrap.service.BootstrapService;
import com.example.find_my_edge.schema.dto.SchemaResponseDto;
import com.example.find_my_edge.schema.mapper.SchemaDtoMapper;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BootstrapServiceImpl implements BootstrapService {

    private final ComputeService computeService;

    private final SchemaService schemaService;
    private final TradeService tradeService;

    private final SchemaDtoMapper schemaDtoMapper;

    private TradeContextBuilder tradeContextBuilder;


    @Override
    public BootstrapResponse init() {
        ComputationContext ctx = tradeContextBuilder.buildContext();

        List<String> schemaOrder = ctx.getSchemaOrder();
        Map<String, Schema> schemas = ctx.getSchemasById();

        Map<String, SchemaResponseDto> schemasById =
                schemas.entrySet()
                       .stream()
                       .collect(Collectors.toMap(
                               Map.Entry::getKey,
                               e -> schemaDtoMapper.toResponse(e.getValue())
                       ));

        List<String> tradesOrder = ctx.getTradeOrder();

        Map<String, Map<String, Object>> raw = ctx.getRaw();
        Map<String, Map<String, Object>> computed = ctx.getComputed();

        return BootstrapResponse.builder()
                                .schemasById(schemasById)
                                .schemasOrder(schemaOrder)
                                .tradesById(raw)
                                .derivedByTradeId(computed)
                                .tradesOrder(tradesOrder)
                                .build();
    }
}
