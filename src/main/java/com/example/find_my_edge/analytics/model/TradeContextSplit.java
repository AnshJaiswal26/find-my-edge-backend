package com.example.find_my_edge.analytics.model;

import java.util.List;
import java.util.Map;

public class TradeContextSplit {

    private final Map<String, Map<String, Object>> raw;
    private final Map<String, Map<String, Object>> computed;
    private final List<String> tradesOrder;

    public TradeContextSplit(
            Map<String, Map<String, Object>> raw,
            Map<String, Map<String, Object>> computed,
            List<String> tradesOrder
    ) {
        this.raw = raw;
        this.computed = computed;
        this.tradesOrder = tradesOrder;
    }

    public Map<String, Map<String, Object>> getRaw() {
        return raw;
    }

    public Map<String, Map<String, Object>> getComputed() {
        return computed;
    }

    public List<String> getTradesOrder() {
        return tradesOrder;
    }
}