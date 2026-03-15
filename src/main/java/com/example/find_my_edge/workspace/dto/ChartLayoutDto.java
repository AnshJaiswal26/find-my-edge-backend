package com.example.find_my_edge.workspace.dto;

import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import lombok.Getter;

import java.util.Map;

@Getter
public class ChartLayoutDto {
    Map<String, Object> layout;
    Map<String, SeriesConfig> seriesById;
}
