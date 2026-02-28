package com.example.find_my_edge.application.dashboard.service.impl;

import com.example.find_my_edge.analytics.model.TradeContextSplit;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.api.schema.dto.SchemaResponseDto;
import com.example.find_my_edge.api.schema.mapper.SchemaDtoMapper;
import com.example.find_my_edge.application.dashboard.model.DashboardData;
import com.example.find_my_edge.application.dashboard.service.DashboardService;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import com.example.find_my_edge.domain.schema.service.SchemaService;
import com.example.find_my_edge.domain.trade.model.Trade;
import com.example.find_my_edge.domain.trade.service.TradeService;
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

        List<String> schemasOrder = schemaBundle.getSchemasOrder();
        Map<String, Schema> schemas = schemaBundle.getSchemasById();

        TradeContextSplit tradeContextSplit =
                computeService.getTradeContextSplit(schemas, trades);

        Map<String, SchemaResponseDto> schemasById = new HashMap<>();

        schemas.forEach((key, value) ->
                                schemasById.put(key, schemaDtoMapper.toResponse(value))
        );

        List<String> tradesOrder = tradeContextSplit.getTradesOrder();

        Map<String, Map<String, Object>> raw = tradeContextSplit.getRaw();
        Map<String, Map<String, Object>> computed = tradeContextSplit.getComputed();

        Map<String, ChartConfig> charts = page.getCharts();
        List<String> chartOrder = page.getChartOrder();

        Map<String, StatConfig> statsById = page.getStatsById();
        List<String> statsOrder = page.getStatsOrder();

        Map<String, ChartLayoutConfig> chartGridLayout = page.getChartGridLayout();

        computeStats(statsById, schemas, trades);

        return DashboardData.builder()
                            .chartGridLayout(chartGridLayout)
                            .schemasById(schemasById)
                            .schemasOrder(schemasOrder)
                            .tradesOrder(tradesOrder)
                            .tradesById(raw)
                            .derivedByTradeId(computed)
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

        Map<String, String> userStats = new HashMap<>();
        Map<String, AstConfig> systemStats = new HashMap<>();

        for (Map.Entry<String, StatConfig> entry : statsById.entrySet()) {
            if (statRegistry.has(entry.getKey())) {
                systemStats.put(entry.getKey(), entry.getValue().getAst());
            } else {
                userStats.put(entry.getKey(), entry.getValue().getFormula());
            }
        }

        Map<String, Double> userStatResults = computeService.computeAggregateForFormulas(
                userStats, schemas, trades
        );

        Map<String, Double> systemStatResults = computeService.computeAggregateForAstConfigs(
                systemStats, schemas, trades
        );

        for (Map.Entry<String, StatConfig> entry : statsById.entrySet()) {
            String key = entry.getKey();

            Double value = systemStatResults.containsKey(key)
                           ? systemStatResults.get(key)
                           : userStatResults.get(key);

            entry.getValue().setValue(value);
        }
    }
}
