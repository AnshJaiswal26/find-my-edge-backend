package com.example.find_my_edge.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class RecomputeResult {

    private Map<String, Double> statValues;

    private Map<String, Map<String, Double>>  seriesValues;

    private Map<String, ChartResult> groupSeriesAggregateResult;

    private Map<String, Map<String, Object>> tradeUpdates;
}