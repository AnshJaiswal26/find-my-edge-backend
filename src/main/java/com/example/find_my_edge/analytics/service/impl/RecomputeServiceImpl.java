package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.service.ComputeService;
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
    private final ComputeService computeService;
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

        computeService.executeAggregate(
                affectedStats,
                StatConfig::getId,
                (id, stat) -> stat.getFormula(),
                (id, stat) -> stat.getAst(),
                (id, value) -> statsById.get(id).setValue(value),
                ctx
        );

        /*
         * ---------------- FIND AFFECTED CHART SERIES ----------------
         */

        List<SeriesConfig> affectedSeries = new ArrayList<>();

        for (ChartConfig chart : chartsById.values()) {

            if (chart.getSeriesConfig() == null) continue;

            chart.getSeriesConfig()
                 .stream()
                 .filter(series ->
                                 series.getDependencies() != null &&
                                 series.getDependencies()
                                       .stream()
                                       .anyMatch(affectedSchemas::contains))
                 .forEach(affectedSeries::add);
        }

        Map<String, SeriesConfig> seriesByKey =
                affectedSeries.stream()
                              .collect(Collectors.toMap(
                                      SeriesConfig::getKey,
                                      s -> s
                              ));

        computeService.executeAggregate(
                affectedSeries,
                SeriesConfig::getKey,
                (id, series) -> series.getFormula(),
                (id, series) -> series.getAst(),
                (id, value) -> seriesByKey.get(id).setValue(value),
                ctx
        );

        /*
         * ---------------- BUILD TRADE UPDATES ----------------
         */

        Map<String, Map<String, Object>> tradeUpdates = new HashMap<>();

        Map<String, Map<String, Object>> computed = ctx.getComputed();

        for (String tradeId : ctx.getTradeOrder()) {

            Map<String, Object> rowComputed = computed.get(tradeId);
            if (rowComputed == null) continue;

            Map<String, Object> filtered = rowComputed.entrySet()
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
                affectedStats,
                affectedSeries,
                tradeUpdates
        );
    }


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
                     .filter(stat ->
                                     stat.getDependencies() != null &&
                                     stat.getDependencies().contains(changedField))
                     .toList();

        computeService.executeAggregate(
                affectedStats,
                StatConfig::getId,
                (id, stat) -> stat.getSource() != Source.SYSTEM ? stat.getFormula() : null,
                (id, stat) -> stat.getSource() == Source.SYSTEM ? stat.getAst() : null,
                (id, value) -> stats.get(id).setValue(value),
                ctx
        );

        List<SeriesConfig> affectedSeries = new ArrayList<>();
        Map<String, Boolean> systemSeries = new HashMap<>();

        for (ChartConfig chart : charts.values()) {

            if (chart.getSeriesConfig() == null) continue;

            boolean usesAst = chart.getMeta().getSource() == Source.SYSTEM;

            chart.getSeriesConfig()
                 .stream()
                 .filter(series ->
                                 series.getDependencies() != null &&
                                 series.getDependencies().contains(changedField))
                 .forEach(s -> {
                     affectedSeries.add(s);
                     systemSeries.put(s.getId(), usesAst);
                 });
        }

        Map<String, SeriesConfig> seriesByKey =
                affectedSeries.stream()
                              .collect(Collectors.toMap(
                                      SeriesConfig::getId,
                                      s -> s
                              ));

        computeService.executeAggregate(
                affectedSeries,
                SeriesConfig::getId,
                (id, s) ->
                        Boolean.FALSE.equals(systemSeries.get(id)) ? s.getFormula() : null,
                (id, s) ->
                        Boolean.TRUE.equals(systemSeries.get(id)) ? s.getAst() : null,
                (id, value) -> seriesByKey.get(id).setValue(value),
                ctx
        );

        List<String> tradeOrder = ctx.getTradeOrder();

        int index = tradeOrder.indexOf(changedTradeId);

        Map<String, Map<String, Object>> updates = new HashMap<>();

        for (int i = index; i < tradeOrder.size(); i++) {

            String tradeId = tradeOrder.get(i);

            updates.put(
                    tradeId,
                    ctx.getComputed().get(tradeId)
            );
        }

        return new RecomputeResult(
                affectedStats,
                affectedSeries,
                updates
        );
    }
}