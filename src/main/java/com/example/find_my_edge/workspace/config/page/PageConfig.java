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
    private Map<String, ChartLayoutConfig> chartGridLayout = new HashMap<>();
    private Map<String, ChartConfig> charts = new HashMap<>();
    private List<String> chartOrder = new ArrayList<>();

    // Stats
    private List<String> statsOrder = new ArrayList<>();
    private Map<String, StatConfig> statsById = new HashMap<>();

    // Table
    private List<String> columnsOrder = new ArrayList<>();
    private Map<String, Integer> columnWidths = new HashMap<>();

}
