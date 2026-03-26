package com.example.find_my_edge.bootstrap.service.impl;

import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.bootstrap.dto.BootstrapResponse;
import com.example.find_my_edge.bootstrap.service.BootstrapService;
import com.example.find_my_edge.schema.dto.SchemaResponse;
import com.example.find_my_edge.schema.mapper.SchemaDtoMapper;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.trade_setup.dto.TradeSetupResponse;
import com.example.find_my_edge.trade_setup.mapper.TradeSetupDtoMapper;
import com.example.find_my_edge.trade_setup.model.TradeSetup;
import com.example.find_my_edge.trade_setup.service.TradeSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BootstrapServiceImpl implements BootstrapService {

    private final SchemaDtoMapper schemaDtoMapper;

    private final TradeContextBuilder tradeContextBuilder;

    private final TradeSetupService tradeSetupService;

    private final TradeSetupDtoMapper tradeSetupDtoMapper;

//    private final SetupScoreComputeService setupScoreComputeService;


    @Override
    public BootstrapResponse init() {
        ComputationContext ctx = tradeContextBuilder.buildContext();

        List<String> schemaOrder = ctx.getSchemaOrder();
        Map<String, Schema> schemas = ctx.getSchemasById();

        Map<String, SchemaResponse> schemasById =
                schemas.entrySet()
                       .stream()
                       .collect(Collectors.toMap(
                               Map.Entry::getKey,
                               e -> schemaDtoMapper.toResponse(e.getValue())
                       ));

        List<String> tradesOrder = ctx.getTradeOrder();

        Map<String, Map<String, Object>> raw = ctx.getRaw();
        Map<String, Map<String, Object>> computed = ctx.getComputed();

        List<TradeSetup> tradeSetups = tradeSetupService.getAll();

//        Map<String, Map<String, EvaluationResult>> setupScoreResult =
//                setupScoreComputeService.computeAllScores(tradeSetups, ctx);

        List<String> setupOrders = new ArrayList<>();
        Map<String, TradeSetupResponse> setupsById = new HashMap<>();

        tradeSetups.forEach(setup -> {
            setupOrders.add(setup.getId());
            setupsById.put(setup.getId(), tradeSetupDtoMapper.toResponse(setup));

        });

        return BootstrapResponse.builder()
                                .schemasById(schemasById)
                                .schemasOrder(schemaOrder)
                                .tradesById(raw)
                                .tradeSetupsOrder(setupOrders)
                                .tradeSetupsById(setupsById)
                                .setupScoreResult(ctx.getScoreResults())
                                .derivedByTradeId(computed)
                                .tradesOrder(tradesOrder)
                                .build();
    }
}
