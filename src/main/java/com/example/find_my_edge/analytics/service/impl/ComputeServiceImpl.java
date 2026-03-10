package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.aggregate.AggregateComputeEngine;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.engine.dataSet.GlobalTradeDataset;
import com.example.find_my_edge.analytics.engine.dataSet.GroupTradeDataset;
import com.example.find_my_edge.analytics.engine.dataSet.TradeDataset;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.analytics.service.RecomputeService;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ComputeServiceImpl implements ComputeService {

    private final AggregateComputeEngine aggregateComputeEngine;
    private final TradeContextBuilder tradeContextBuilder;

    private final GroupBuilder groupBuilder;

    private final RecomputeService recomputeService;

    private final ExecutorService executorService;

    @Override
    public Map<String, Double> computeAggregatePerGroupByAstConfigs(
            GroupConfig groupConfig,
            AstConfig astConfig
    ) {
        ComputationContext ctx = tradeContextBuilder.buildContext();

        return executeGroupAggregate(groupConfig, astConfig, null, ctx);
    }

    @Override
    public Map<String, Double> computeAggregatePerGroupByFormula(
            GroupConfig groupConfig,
            String formula
    ) {
        ComputationContext ctx = tradeContextBuilder.buildContext();

        return executeGroupAggregate(groupConfig, null, formula, ctx);
    }

    public Map<String, Double> executeGroupAggregate(
            GroupConfig groupConfig,
            AstConfig astConfig,
            String formula,
            ComputationContext ctx
    ) {

        Map<String, Schema> schemasById = ctx.getSchemasById();
        List<String> tradeOrder = ctx.getTradeOrder();

        Map<String, Map<String, Object>> raw = ctx.getRaw();
        Map<String, Map<String, Object>> computed = ctx.getComputed();

        List<Group> groups = groupBuilder.buildGroups(
                tradeOrder,
                groupConfig,
                (tradeId, key) -> {
                    Object value = raw.get(tradeId).get(key);
                    if (value == null) {
                        return computed.get(tradeId).get(key);
                    }
                    return value;
                }
        );

        Map<String, Future<Double>> futures = new HashMap<>();

        for (Group group : groups) {
            futures.put(
                    group.getGroupId(),
                    executorService.submit(
                            () -> aggregateComputeEngine.computedAggregate(
                                    astConfig,
                                    formula,
                                    schemasById,
                                    new GroupTradeDataset(
                                            ctx,
                                            group.getTradeIds()
                                    )
                            )
                    )
            );
        }

        return collectFutures(futures);
    }

    private Map<String, Double> collectFutures(Map<String, Future<Double>> futures) {
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Future<Double>> entry : futures.entrySet()) {
            try {
                result.put(entry.getKey(), entry.getValue().get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AstException("Thread interrupted", e);
            } catch (ExecutionException e) {
                throw new AstException(
                        "Error computing key: " + entry.getKey(),
                        e.getCause()
                );
            }
        }

        return result;
    }

}