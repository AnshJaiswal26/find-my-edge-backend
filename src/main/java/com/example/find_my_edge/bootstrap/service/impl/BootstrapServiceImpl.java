package com.example.find_my_edge.bootstrap.service.impl;

import com.example.find_my_edge.analytics.model.TradeContextSplit;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.bootstrap.dto.BootstrapResponse;
import com.example.find_my_edge.bootstrap.service.BootstrapService;
import com.example.find_my_edge.schema.dto.SchemaResponseDto;
import com.example.find_my_edge.schema.mapper.SchemaDtoMapper;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaBundle;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BootstrapServiceImpl implements BootstrapService {

    private final ComputeService computeService;

    private final SchemaService schemaService;
    private final TradeService tradeService;

    private final SchemaDtoMapper schemaDtoMapper;

    @Override
    public BootstrapResponse init() {

        SchemaBundle schemaBundle = schemaService.getAll();
        List<Trade> trades = tradeService.getAll();

        List<String> schemasOrder = schemaBundle.getSchemasOrder();
        Map<String, Schema> schemas = schemaBundle.getSchemasById();

        TradeContextSplit tradeContextSplit =
                computeService.getTradeContextSplit(schemas, trades);

        Map<String, SchemaResponseDto> schemasById = new HashMap<>();

        schemas.forEach((key, value) ->
                                schemasById.put(key, schemaDtoMapper.toResponse(value)));

        List<String> tradesOrder = tradeContextSplit.getTradesOrder();

        Map<String, Map<String, Object>> raw = tradeContextSplit.getRaw();
        Map<String, Map<String, Object>> computed = tradeContextSplit.getComputed();

        return BootstrapResponse.builder()
                                .schemasById(schemasById)
                                .schemasOrder(schemasOrder)
                                .tradesById(raw)
                                .derivedByTradeId(computed)
                                .tradesOrder(tradesOrder)
                                .build();
    }
}
