package com.example.find_my_edge.analytics.model;

import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeriesUpdate {

    private String chartId;
    private SeriesConfig series;

}