package com.example.find_my_edge.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ComputationContext {

    private final Map<String, Map<String, Object>> raw;
    private final Map<String, Map<String, Object>> computed;
    private final List<String> tradeOrder;

}