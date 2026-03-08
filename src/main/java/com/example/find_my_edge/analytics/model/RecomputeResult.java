package com.example.find_my_edge.analytics.model;

import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RecomputeResult {

    private List<StatConfig> stats;

    private List<SeriesConfig> series;

    private Map<String, Map<String, Object>> computedTrades;
}