package com.example.find_my_edge.analytics.service.impl;


import com.example.find_my_edge.analytics.compute.ChartComputeService;
import com.example.find_my_edge.analytics.compute.StatComputeService;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.model.SeriesUpdate;
import com.example.find_my_edge.analytics.service.RecomputeService;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
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

        return new RecomputeResult(null, null, trades);
    }

    @Override
    public RecomputeResult recomputeOnDefinitionChange(
            String pageName,
            String changedMetricId
    ) {

        PageConfig page = workspaceService.getPage(pageName);

        Map<String, StatConfig> statsById = page.getStatsById();
        Map<String, ChartConfig> chartsById = page.getCharts();

        ComputationContext ctx = tradeContextBuilder.buildContext();

        Map<String, Schema> schemasById = ctx.getSchemasById();
        List<String> schemaOrder = ctx.getSchemaOrder();

        /*
         * ---------------- RESOLVE AFFECTED SCHEMAS ----------------
         */

        Set<String> affectedSchemas =
                resolveAffectedSchemas(schemaOrder, schemasById, changedMetricId);

        /*
         * ---------------- FIND AFFECTED STATS ----------------
         */

        List<StatConfig> affectedStats =
                statsById.values()
                         .stream()
                         .filter(stat ->
                                         stat.getDependencies() != null &&
                                         stat.getDependencies()
                                             .stream()
                                             .anyMatch(affectedSchemas::contains))
                         .toList();

        Map<String, Double> statValues =
                statComputeService.computeStats(affectedStats, ctx);

        /*
         * ---------------- FIND AFFECTED CHART SERIES ----------------
         */

        Map<String, Boolean> systemSeries = new HashMap<>();

        List<SeriesUpdate> affectedSeries =
                chartComputeService.resolveSeries(chartsById, systemSeries, affectedSchemas);

        Map<String, Double> seriesValues =
                chartComputeService.computeSeries(affectedSeries, systemSeries, ctx);

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

        return new RecomputeResult(
                statValues,
                seriesValues,
                tradeUpdates
        );
    }


    @Override
    public RecomputeResult recomputeByTradeField(
            String pageName,
            String changedField,
            String changedTradeId
    ) {

        PageConfig page = workspaceService.getPage(pageName);

        ComputationContext ctx = tradeContextBuilder.buildContext();

        List<String> schemaOrder = ctx.getSchemaOrder();
        Map<String, Schema> schemasById = ctx.getSchemasById();

        Map<String, StatConfig> stats = page.getStatsById();
        Map<String, ChartConfig> charts = page.getCharts();

        Set<String> affectedSchemas =
                resolveAffectedSchemas(schemaOrder, schemasById, changedField);


        List<StatConfig> affectedStats =
                stats.values()
                     .stream()
                     .filter(s -> s.getDependencies() != null &&
                                  s.getDependencies().stream().anyMatch(affectedSchemas::contains))
                     .toList();


        Map<String, Double> statsValues =
                statComputeService.computeStats(affectedStats, ctx);

        Map<String, Boolean> systemSeries = new HashMap<>();

        List<SeriesUpdate> affectedSeries =
                chartComputeService.resolveSeries(charts, systemSeries, affectedSchemas);

        Map<String, Double> seriesValues =
                chartComputeService.computeSeries(affectedSeries, systemSeries, ctx);

        List<String> tradeOrder = ctx.getTradeOrder();
        int index = tradeOrder.indexOf(changedTradeId);

        Map<String, Map<String, Object>> updates = new HashMap<>();

        for (String schemaId : affectedSchemas) {
            Schema schema = schemasById.get(schemaId);

            if (!schema.isComputed()) continue;

            if (schema.hasWindowFunction()) {
                for (int i = index; i < tradeOrder.size(); i++) {
                    String tradeId = tradeOrder.get(i);
                    updateAffectedTrades(updates, schemaId, tradeId, ctx);
                }
            } else {
                updateAffectedTrades(updates, schemaId, changedTradeId, ctx);
            }
        }

        return new RecomputeResult(statsValues, seriesValues, updates);
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


    private Set<String> resolveAffectedSchemas(
            List<String> schemaOrder,
            Map<String, Schema> schemasById,
            String changedMetricId
    ) {
        Set<String> affectedSchemas = new LinkedHashSet<>();

        for (String schemaId : schemaOrder) {

            Schema schema = schemasById.get(schemaId);
            if (schema == null || schema.getDependencies() == null) continue;

            if (schema.getDependencies().contains(changedMetricId)
                || affectedSchemas.stream()
                                  .anyMatch(dep -> schema.getDependencies().contains(dep))) {

                affectedSchemas.add(schemaId);
            }
        }
        return affectedSchemas;
    }


}