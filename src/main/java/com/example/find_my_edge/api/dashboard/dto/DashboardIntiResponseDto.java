package com.example.find_my_edge.api.dashboard.dto;

import com.example.find_my_edge.api.schema.dto.SchemaResponseDto;
import com.example.find_my_edge.api.trade.dto.TradeResponseDto;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartLayoutConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardIntiResponseDto {

    // Global Trade Store
    private Map<String , SchemaResponseDto> schemasById;

    private List<String> schemasOrder;

    private Map<String, TradeResponseDto> tradesById;

    private Map<String, TradeResponseDto> derivedByTradeId;

    private List<String> tradesOrder;


    // Dashboard Store
    private Map<String, ChartLayoutConfig> chartGridLayout;

    private Map<String, ChartConfig> charts;

    private List<String> chartOrder;

    private Map<String, StatConfig> statsById;

    private List<String> statsOrder;

}
