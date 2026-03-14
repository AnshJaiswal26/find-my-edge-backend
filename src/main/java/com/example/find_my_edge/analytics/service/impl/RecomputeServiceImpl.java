package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.ast.util.DependencyResolver;
import com.example.find_my_edge.analytics.compute.ChartComputeService;
import com.example.find_my_edge.analytics.compute.StatComputeService;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.service.RecomputeService;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecomputeServiceImpl implements RecomputeService {

    private final WorkspaceService workspaceService;

    private final TradeContextBuilder tradeContextBuilder;

    private final ChartComputeService chartComputeService;

    private final StatComputeService statComputeService;

    private final DependencyResolver dependencyResolver;

    @Override
    public RecomputeResult recomputeOnSchemaCreation(String schemaId) {
        ComputationContext ctx = tradeContextBuilder.buildContext();

        Map<String, Map<String, Object>> computed = ctx.getComputed();

        Map<String, Map<String, Object>> trades =
                computed.entrySet()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> Map.of(schemaId, e.getValue().get(schemaId))
                                )
                        );

        return new RecomputeResult(null, null, null, trades);
    }

    @Override
    public RecomputeResult recomputeOnDefinitionChange(
            String pageName,
            String changedMetricId
    ) {

        ComputationContext ctx = tradeContextBuilder.buildContext();

        /*
         * ---------------- RESOLVE AFFECTED SCHEMAS ----------------
         */

        Set<String> affectedSchemas = dependencyResolver.resolveAffectedSchemas(
                ctx.getSchemaOrder(),
                ctx.getSchemasById(),
                changedMetricId
        );

        RecomputeResult recomputeResult = recomputeChartsAndStats(pageName, affectedSchemas, ctx);


        /*
         * ---------------- BUILD TRADE UPDATES ----------------
         */

        Map<String, Map<String, Object>> tradeUpdates = new HashMap<>();

        Map<String, Map<String, Object>> computed = ctx.getComputed();

        for (String tradeId : ctx.getTradeOrder()) {

            Map<String, Object> rowComputed = computed.get(tradeId);
            if (rowComputed == null) continue;

            Map<String, Object> filtered =
                    rowComputed.entrySet()
                               .stream()
                               .filter(e -> affectedSchemas.contains(e.getKey()))
                               .collect(Collectors.toMap(
                                       Map.Entry::getKey,
                                       Map.Entry::getValue
                               ));

            if (!filtered.isEmpty()) {
                tradeUpdates.put(tradeId, filtered);
            }
        }

        recomputeResult.setTradeUpdates(tradeUpdates);

        return recomputeResult;
    }


    @Override
    public RecomputeResult recomputeByTradeField(
            String pageName,
            String changedField,
            String changedTradeId
    ) {

        ComputationContext ctx = tradeContextBuilder.buildContext();

        Map<String, Schema> schemasById = ctx.getSchemasById();

        Set<String> affectedSchemas = dependencyResolver.resolveAffectedSchemas(
                ctx.getSchemaOrder(),
                schemasById,
                changedField
        );

        RecomputeResult recomputeResult = recomputeChartsAndStats(pageName, affectedSchemas, ctx);

        List<String> tradeOrder = ctx.getTradeOrder();
        int index = tradeOrder.indexOf(changedTradeId);

        Map<String, Map<String, Object>> tradeUpdates = new HashMap<>();

        for (String schemaId : affectedSchemas) {
            Schema schema = schemasById.get(schemaId);

            if (!schema.isComputed()) continue;

            if (schema.hasWindowFunction()) {
                for (int i = index; i < tradeOrder.size(); i++) {
                    String tradeId = tradeOrder.get(i);
                    updateAffectedTrades(tradeUpdates, schemaId, tradeId, ctx);
                }
            } else {
                updateAffectedTrades(tradeUpdates, schemaId, changedTradeId, ctx);
            }
        }

        recomputeResult.setTradeUpdates(tradeUpdates);

        return recomputeResult;
    }

    private RecomputeResult recomputeChartsAndStats(
            String pageName,
            Set<String> affectedSchemas,
            ComputationContext ctx
    ) {
        PageConfig page = workspaceService.getPage(pageName);

        Map<String, StatConfig> statsById = page.getStatsById();
        Map<String, ChartConfig> chartsById = page.getCharts();


        // ---------------- FIND AFFECTED STATS AND COMPUTE ----------------
        Map<String, Double> statValues =
                statComputeService.computeStats(statsById, affectedSchemas, ctx);

        // ---------------- FIND AFFECTED CHART AND COMPUTE ----------------

        Map<String, ChartConfig> affectedCharts =
                chartComputeService.resolveAffectedCharts(chartsById, affectedSchemas);

        Map<String, ChartResult> resultMap =
                chartComputeService.computeCharts(chartsById, ctx);

        Map<String, Map<String, Double>> seriesValues =
                affectedCharts
                        .values()
                        .stream()
                        .collect(Collectors.toMap(
                                ChartConfig::getId,
                                chart -> chart.getSeriesById().values()
                                              .stream()
                                              .collect(Collectors.toMap(
                                                      SeriesConfig::getId,
                                                      SeriesConfig::getValue
                                              ))
                        ));

        return new RecomputeResult(statValues, seriesValues, resultMap, null);
    }

    private void updateAffectedTrades(
            Map<String, Map<String, Object>> updates,
            String schemaId,
            String changedTradeId,
            ComputationContext ctx
    ) {
        Map<String, Object> computedTrade = ctx.getComputed().get(changedTradeId);
        Map<String, Object> trade = updates.get(changedTradeId);

        if (trade != null) {
            trade.put(schemaId, computedTrade.get(schemaId));
        } else {
            updates.put(changedTradeId, new HashMap<>(Map.of(schemaId, computedTrade.get(schemaId))));
        }
    }
}