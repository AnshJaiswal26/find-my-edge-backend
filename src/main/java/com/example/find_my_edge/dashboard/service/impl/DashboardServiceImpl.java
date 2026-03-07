package com.example.find_my_edge.dashboard.service.impl;

import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.schema.mapper.SchemaDtoMapper;
import com.example.find_my_edge.dashboard.model.DashboardData;
import com.example.find_my_edge.dashboard.service.DashboardService;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaBundle;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.service.TradeService;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartLayoutConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.enums.PageType;
import com.example.find_my_edge.workspace.exception.PageNotFoundException;
import com.example.find_my_edge.workspace.registry.StatRegistry;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final WorkspaceService workspaceService;
    private final ComputeService computeService;

    private final SchemaService schemaService;
    private final TradeService tradeService;

    private final SchemaDtoMapper schemaDtoMapper;

    private final StatRegistry statRegistry;

    @Override
    public DashboardData init() {

        SchemaBundle schemaBundle = schemaService.getAll();
        List<Trade> trades = tradeService.getAll();
        PageConfig page = workspaceService.getPage(PageType.DASHBOARD.key());

        if (page == null) {
            throw new PageNotFoundException("Dashboard page config not found");
        }
        Map<String, Schema> schemas = schemaBundle.getSchemasById();

        Map<String, ChartConfig> charts = page.getCharts();
        List<String> chartOrder = page.getChartOrder();

        Map<String, StatConfig> statsById = page.getStatsById();
        List<String> statsOrder = page.getStatsOrder();

        Map<String, ChartLayoutConfig> chartGridLayout = page.getChartGridLayout();

        computeStats(statsById, schemas, trades);

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
            Map<String, Schema> schemas,
            List<Trade> trades
    ) {

        statsById.forEach((k, v) -> {

        });

        computeService.executeAggregate(
                statsById,
                id -> !statRegistry.has(id) ? statsById.get(id).getFormula() : null,
                id -> statRegistry.has(id) ? statsById.get(id).getAst() : null,
                (id, value) -> {
                    StatConfig statConfig = statsById.get(id);
                    statConfig.setValue(value);
                },
                schemas,
                trades
        );
    }
}
