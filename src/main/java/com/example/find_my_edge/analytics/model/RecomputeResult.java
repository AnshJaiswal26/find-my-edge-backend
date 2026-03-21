package com.example.find_my_edge.analytics.model;

import com.example.find_my_edge.trade_setup.model.EvaluationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RecomputeResult {

    private Map<String, Double> statValues;

    private Map<String, Map<String, Double>>  seriesValues;

    private Map<String, ChartResult> groupSeriesAggregateResult;

    private Map<String, Map<String, Object>> tradeUpdates;

    private Map<String, Map<String, EvaluationResult>> setupResults;
}