package com.example.find_my_edge.dashboard.model;

import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartLayoutConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardData {

    // Dashboard Store
    private Map<String, ChartLayoutConfig> chartGridLayout;

    private Map<String, ChartConfig> charts;
    private Map<String, ChartResult> groupAggregateChartResult;

    private List<String> chartOrder;

    private Map<String, StatConfig> statsById;

    private List<String> statsOrder;

}
