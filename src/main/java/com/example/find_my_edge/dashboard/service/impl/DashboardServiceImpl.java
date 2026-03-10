package com.example.find_my_edge.dashboard.service.impl;

import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.service.AggregateExecutionService;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.dashboard.model.DashboardData;
import com.example.find_my_edge.dashboard.service.DashboardService;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartLayoutConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.enums.PageType;
import com.example.find_my_edge.workspace.enums.Source;
import com.example.find_my_edge.workspace.exception.PageNotFoundException;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final WorkspaceService workspaceService;
    private final ComputeService computeService;

    private final TradeContextBuilder tradeContextBuilder;

    private final AggregateExecutionService aggregateExecutionService;

    @Override
    public DashboardData init() {

        ComputationContext computationContext = tradeContextBuilder.buildContext();

        PageConfig page = workspaceService.getPage(PageType.DASHBOARD.key());

        if (page == null) {
            throw new PageNotFoundException("Dashboard page config not found");
        }

        Map<String, ChartConfig> charts = page.getCharts();
        List<String> chartOrder = page.getChartOrder();

        Map<String, StatConfig> statsById = page.getStatsById();
        List<String> statsOrder = page.getStatsOrder();

        Map<String, ChartLayoutConfig> chartGridLayout = page.getChartGridLayout();

        computeStats(statsById, computationContext);
        computeCharts(charts, computationContext);

        return DashboardData.builder()
                            .chartGridLayout(chartGridLayout)
                            .charts(charts)
                            .chartOrder(chartOrder)
                            .statsById(statsById)
                            .statsOrder(statsOrder)
                            .build();
    }

    public void computeStats(
            Map<String, StatConfig> statsById,
            ComputationContext computationContext
    ) {

        aggregateExecutionService.executeAggregate(
                statsById.entrySet(),
                Map.Entry::getKey,
                (id, entry) ->
                        entry.getValue().getSource() != Source.SYSTEM
                        ? entry.getValue().getFormula()
                        : null,
                (id, entry) ->
                        entry.getValue().getSource() == Source.SYSTEM
                        ? entry.getValue().getAst()
                        : null,
                (id, value) -> {
                    StatConfig statConfig = statsById.get(id);
                    statConfig.setValue(value);
                },
                computationContext
        );
    }

    public void computeCharts(
            Map<String, ChartConfig> chartsById,
            ComputationContext computationContext
    ) {

        chartsById.forEach((chartId, chart) -> {

            if (chart.getCategory() == ChartCategory.SERIES) return;

            boolean useAst = chart.getSource() == Source.SYSTEM;

            Map<String, SeriesConfig> configByKey =
                    chart.getSeries().stream()
                         .collect(Collectors.toMap(SeriesConfig::getField, s -> s));

            aggregateExecutionService.executeAggregate(
                    chart.getSeries(),
                    SeriesConfig::getField,
                    (id, cfg) -> !useAst ? cfg.getFormula() : null,
                    (id, cfg) -> useAst ? cfg.getAst() : null,
                    (id, value) ->
                            configByKey.get(id).setValue(value),
                    computationContext
            );
        });
    }
}
