package com.example.find_my_edge.analytics.service;

import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.trade.model.Trade;

import java.util.List;
import java.util.Map;

public interface ComputeService {

    Map<String, Map<String, Double>> getTradeContext(
            Map<String, Schema> schemasById,
            List<Trade> trades
    );


    Map<String, Double> computeAggregateFromFormulas(Map<String, String> formulas);
}
