package com.example.find_my_edge.analytics.service.impl;


import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.model.SeriesUpdate;
import com.example.find_my_edge.analytics.service.AggregateExecutionService;
import com.example.find_my_edge.analytics.service.RecomputeService;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.enums.Source;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecomputeServiceImpl implements RecomputeService {

    private final WorkspaceService workspaceService;

    private final AggregateExecutionService aggregateExecutionService;

    private final TradeContextBuilder tradeContextBuilder;

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

        Map<String, Double> statValues = computeStats(affectedStats, ctx);

        /*
         * ---------------- FIND AFFECTED CHART SERIES ----------------
         */

        Map<String, Boolean> systemSeries = new HashMap<>();

        List<SeriesUpdate> affectedSeries =
                resolveSeries(chartsById, systemSeries, changedMetricId);

        Map<String, Double> seriesValues = computeSeries(affectedSeries, systemSeries, ctx);

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

        Map<String, StatConfig> stats = page.getStatsById();
        Map<String, ChartConfig> charts = page.getCharts();

        List<StatConfig> affectedStats =
                stats.values()
                     .stream()
                     .filter(s -> s.getDependencies() != null &&
                                  s.getDependencies().contains(changedField))
                     .toList();


        Map<String, Double> statsValues = computeStats(affectedStats, ctx);

        Map<String, Boolean> systemSeries = new HashMap<>();

        List<SeriesUpdate> affectedSeries =
                resolveSeries(charts, systemSeries, changedField);

        Map<String, Double> seriesValues = computeSeries(affectedSeries, systemSeries, ctx);

        List<String> tradeOrder = ctx.getTradeOrder();
        int index = tradeOrder.indexOf(changedTradeId);

        Map<String, Map<String, Object>> updates = new HashMap<>();

        for (int i = index; i < tradeOrder.size(); i++) {
            String tradeId = tradeOrder.get(i);
            updates.put(tradeId, ctx.getComputed().get(tradeId));
        }

        return new RecomputeResult(statsValues, seriesValues, updates);
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

    private Map<String, Double> computeStats(
            List<StatConfig> stats,
            ComputationContext ctx
    ) {

        Map<String, Double> statsValues = new HashMap<>();

        aggregateExecutionService.executeAggregate(
                stats,
                StatConfig::getId,
                (id, stat) -> stat.getSource() != Source.SYSTEM ? stat.getFormula() : null,
                (id, stat) -> stat.getSource() == Source.SYSTEM ? stat.getAst() : null,
                statsValues::put,
                ctx
        );

        return statsValues;
    }

    private List<SeriesUpdate> resolveSeries(
            Map<String, ChartConfig> charts,
            Map<String, Boolean> systemSeries,
            String changedField
    ) {

        List<SeriesUpdate> result = new ArrayList<>();

        for (ChartConfig chart : charts.values()) {

            if (chart.getSeries() == null) continue;

            boolean usesAst = chart.getSource() == Source.SYSTEM;

            chart.getSeries()
                 .stream()
                 .filter(series -> series.getDependencies() != null &&
                                   series.getDependencies().contains(changedField))
                 .forEach(series -> {
                     result.add(new SeriesUpdate(chart.getId(), series));
                     systemSeries.put(series.getId(), usesAst);
                 });
        }

        return result;
    }

    private Map<String, Double> computeSeries(
            List<SeriesUpdate> seriesUpdates,
            Map<String, Boolean> systemSeries,
            ComputationContext ctx
    ) {

        List<SeriesConfig> series =
                seriesUpdates.stream()
                             .map(SeriesUpdate::getSeries)
                             .toList();

        Map<String, Double> seriesValues = new HashMap<>();

        aggregateExecutionService.executeAggregate(
                series,
                SeriesConfig::getId,
                (id, s) -> Boolean.FALSE.equals(systemSeries.get(id)) ? s.getFormula() : null,
                (id, s) -> Boolean.TRUE.equals(systemSeries.get(id)) ? s.getAst() : null,
                seriesValues::put,
                ctx
        );

        return seriesValues;
    }
}