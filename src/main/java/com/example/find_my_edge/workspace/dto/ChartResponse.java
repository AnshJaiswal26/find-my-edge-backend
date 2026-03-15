package com.example.find_my_edge.workspace.dto;

import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartResponse {
    ChartConfig chart;
    ChartResult chartResult;
}
