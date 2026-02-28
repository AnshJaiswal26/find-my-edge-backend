package com.example.find_my_edge.application.dashboard.model;

import com.example.find_my_edge.api.schema.dto.SchemaResponseDto;
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

    // Global Trade Store
    private Map<String, SchemaResponseDto> schemasById;

    private List<String> schemasOrder;

    private Map<String, Map<String, Object>> tradesById;

    private Map<String, Map<String, Object>> derivedByTradeId;

    private List<String> tradesOrder;


    // Dashboard Store
    private Map<String, ChartLayoutConfig> chartGridLayout;

    private Map<String, ChartConfig> charts;

    private List<String> chartOrder;

    private Map<String, StatConfig> statsById;

    private List<String> statsOrder;

}
