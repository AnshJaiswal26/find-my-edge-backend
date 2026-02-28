package com.example.find_my_edge.api.dashboard.dto;

import java.util.List;
import java.util.Map;

public class TradeContextResponseDto {

    private Map<String, Map<String, Object>> raw;
    private Map<String, Map<String, Object>> computed;
    private List<String> tradesOrder;

    public TradeContextResponseDto(
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