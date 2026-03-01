package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.enums.ComputationMode;
import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.ast.executor.AggregateExecutor;
import com.example.find_my_edge.analytics.ast.executor.RowSequenceExecutor;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.mapper.AstNodeMapper;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.analytics.ast.util.DependencyResolver;
import com.example.find_my_edge.analytics.model.TradeContextSplit;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.domain.schema.enums.ComputeMode;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.trade.model.Trade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class ComputeServiceImpl implements ComputeService {

    private final AggregateExecutor aggregateExecutor;
    private final RowSequenceExecutor rowSequenceExecutor;
    private final DependencyResolver dependencyResolver;
    private final AstPipeline astPipeline;

    private final ExecutorService computeExecutor;

    @Getter
    @AllArgsConstructor
    private static class ComputationContext {
        private final Map<String, Map<String, Object>> raw;
        private final Map<String, Map<String, Object>> computed;
        private final List<String> tradeOrder;
    }

    // ============================
    // PUBLIC APIs
    // ============================

    @Override
    public TradeContextSplit getTradeContextSplit(
            Map<String, Schema> schemasById,
            List<Trade> trades
    ) {
        ComputationContext ctx = buildContext(schemasById, trades);

        return new TradeContextSplit(
                ctx.getRaw(),
                ctx.getComputed(),
                ctx.getTradeOrder()
        );
    }

    @Override
    public Map<String, Double> computeAggregateForFormulas(
            Map<String, String> formulas,
            Map<String, Schema> schemasById,
            List<Trade> trades
    ) {
        ComputationContext ctx = buildContext(schemasById, trades);
        return computeAggregate(formulas, null, schemasById, ctx);
    }

    @Override
    public Map<String, Double> computeAggregateForAstConfigs(
            Map<String, AstConfig> astConfigs,
            Map<String, Schema> schemasById,
            List<Trade> trades
    ) {
        ComputationContext ctx = buildContext(schemasById, trades);
        return computeAggregate(null, astConfigs, schemasById, ctx);
    }

    // ============================
    // CORE CONTEXT BUILDER
    // ============================

    private ComputationContext buildContext(
            Map<String, Schema> schemasById,
            List<Trade> trades
    ) {

        Map<String, Map<String, Object>> raw = new HashMap<>();
        Map<String, Map<String, Object>> computed = new HashMap<>();
        List<String> tradeOrder = new ArrayList<>();

        if (trades == null || trades.isEmpty()) {
            return new ComputationContext(raw, computed, tradeOrder);
        }

        // STEP 1: RAW
        for (Trade trade : trades) {
            if (trade == null || trade.getId() == null) continue;

            String tradeId = trade.getId();
            tradeOrder.add(tradeId);

            Map<String, Object> rawMap = new HashMap<>();
            if (trade.getValues() != null) {
                trade.getValues().forEach((k, v) -> {
                    if (k != null) rawMap.put(k, v);
                });
            }

            raw.put(tradeId, rawMap);
            computed.put(tradeId, new HashMap<>());
        }

        // STEP 2: Dependency resolution
        List<String> executionOrder = dependencyResolver.resolveExecutionOrder(
                dependencyResolver.buildDependencyMap(schemasById.values())
        );

        Map<String, SchemaType> schemaTypeMap = buildSchemaTypeMap(schemasById);

        // STEP 3: Row computation
        for (String schemaId : executionOrder) {

            Schema schema = schemasById.get(schemaId);
            if (schema == null || schema.getAst() == null) continue;

            AstNode astNode = AstNodeMapper.toNode(schema.getAst());

            rowSequenceExecutor.execute(
                    astNode,
                    schemaId,
                    0,
                    schema.getInitialValue(),

                    // setter
                    (index, key, val) -> {
                        if (index < 0 || val == null || index >= trades.size()) return;

                        String tradeId = trades.get(index).getId();
                        if (tradeId == null) return;

                        computed.get(tradeId).put(key, val);
                    },

                    // getter
                    (index, key) -> {
                        if (index < 0 || index >= trades.size()) return null;

                        String tradeId = trades.get(index).getId();
                        if (tradeId == null) return null;

                        Map<String, Object> c = computed.get(tradeId);
                        if (c != null && c.containsKey(key)) return c.get(key);

                        Map<String, Object> r = raw.get(tradeId);
                        return r != null ? r.get(key) : null;
                    },

                    trades::size,
                    schemaTypeMap::get,
                    schema.getMode() == ComputeMode.ROW
                    ? ComputationMode.BASE
                    : ComputationMode.WINDOW
            );
        }

        return new ComputationContext(
                Map.copyOf(raw),
                Map.copyOf(computed),
                List.copyOf(tradeOrder)
        );
    }

    // ============================
    // AGGREGATION ENGINE
    // ============================

    private Map<String, Double> computeAggregate(
            Map<String, String> formulas,
            Map<String, AstConfig> astConfigs,
            Map<String, Schema> schemasById,
            ComputationContext ctx
    ) {

        Map<String, AstNode> astMap = buildAstMap(formulas, astConfigs, schemasById);

        List<String> tradeIds = ctx.getTradeOrder();
        Map<String, SchemaType> schemaTypeMap = buildSchemaTypeMap(schemasById);

        Map<String, Future<Double>> futures = new HashMap<>();

        for (Map.Entry<String, AstNode> entry : astMap.entrySet()) {

            String key = entry.getKey();
            AstNode astNode = entry.getValue();

            futures.put(
                    key, computeExecutor.submit(() -> {

                        Object result = aggregateExecutor.execute(
                                astNode,
                                (index, schemaKey) -> {
                                    if (index < 0 || index >= tradeIds.size()) return null;

                                    String tradeId = tradeIds.get(index);
                                    Map<String, Object> computed = ctx.getComputed().get(tradeId);
                                    if (computed != null && computed.containsKey(schemaKey)) {
                                        return computed.get(schemaKey);
                                    }

                                    Map<String, Object> raw = ctx.getRaw().get(tradeId);
                                    return raw != null ? raw.get(schemaKey) : null;
                                },
                                tradeIds::size,
                                schemaTypeMap::get
                        );

                        if (result == null) return null;

                        if (!(result instanceof Number)) {
                            throw new AstException("Non-numeric result for key: " + key);
                        }

                        return ((Number) result).doubleValue();
                    })
            );
        }

        return collectFutures(futures);
    }

    // ============================
    // HELPERS
    // ============================

    private Map<String, AstNode> buildAstMap(
            Map<String, String> formulas,
            Map<String, AstConfig> astConfigs,
            Map<String, Schema> schemasById
    ) {

        Map<String, AstNode> astMap = new HashMap<>();

        if (formulas != null) {
            for (Map.Entry<String, String> entry : formulas.entrySet()) {
                AstResult result = astPipeline.buildAst(
                        entry.getValue(),
                        FunctionMode.AGGREGATE,
                        schemasById
                );
                astMap.put(entry.getKey(), result.getAstNode());
            }
        }

        if (astConfigs != null) {
            for (Map.Entry<String, AstConfig> entry : astConfigs.entrySet()) {
                astMap.put(entry.getKey(), AstNodeMapper.toNode(entry.getValue()));
            }
        }

        return astMap;
    }

    private Map<String, SchemaType> buildSchemaTypeMap(Map<String, Schema> schemas) {
        Map<String, SchemaType> map = new HashMap<>();

        for (Schema s : schemas.values()) {
            if (s == null || s.getId() == null) continue;

            map.put(
                    s.getId(),
                    new SchemaType(
                            s.getDisplay() != null ? s.getDisplay().getFormat() : null,
                            s.getType() != null ? s.getType().toString() : null
                    )
            );
        }

        return map;
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