package com.example.find_my_edge.dashboard.service.impl;

import com.example.find_my_edge.analytics.compute.ChartComputeService;
import com.example.find_my_edge.analytics.compute.StatComputeService;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.dashboard.model.DashboardData;
import com.example.find_my_edge.dashboard.service.DashboardService;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartLayoutConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.enums.PageType;
import com.example.find_my_edge.workspace.exception.PageNotFoundException;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final WorkspaceService workspaceService;

    private final TradeContextBuilder tradeContextBuilder;

    private final StatComputeService statComputeService;

    private final ChartComputeService chartComputeService;

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

        statComputeService.computeStats(statsById, computationContext);

        Map<String, ChartResult> resultMap =
                chartComputeService.computeCharts(charts, computationContext);

        return DashboardData.builder()
                            .chartGridLayout(chartGridLayout)
                            .charts(charts)
                            .groupAggregateChartResult(resultMap)
                            .chartOrder(chartOrder)
                            .statsById(statsById)
                            .statsOrder(statsOrder)
                            .build();
    }

}
