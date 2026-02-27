package com.example.find_my_edge.workspace.config.page;

import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartLayoutConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageConfig {
    // Charts

    @Builder.Default
    private Map<String, ChartLayoutConfig> chartGridLayout = new HashMap<>();
    @Builder.Default
    private Map<String, ChartConfig> charts = new HashMap<>();
    @Builder.Default
    private List<String> chartOrder = new ArrayList<>();

    // Stats
    @Builder.Default
    private List<String> statsOrder = new ArrayList<>();
    @Builder.Default
    private Map<String, StatConfig> statsById = new HashMap<>();

    // Table
    @Builder.Default
    private List<String> columnsOrder = new ArrayList<>();
    @Builder.Default
    private Map<String, Integer> columnWidths = new HashMap<>();

}
