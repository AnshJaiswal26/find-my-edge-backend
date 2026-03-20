package com.example.find_my_edge.trade_setup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class EvaluationResult {

    private double score; // 0–100

    private Map<String, FieldMatch> fieldMatches;
}