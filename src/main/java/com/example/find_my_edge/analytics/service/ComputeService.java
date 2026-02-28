package com.example.find_my_edge.analytics.service;

import com.example.find_my_edge.analytics.model.TradeContextSplit;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.trade.model.Trade;

import java.util.List;
import java.util.Map;

public interface ComputeService {


    TradeContextSplit getTradeContextSplit(
            Map<String, Schema> schemasById,
            List<Trade> trades
    );

    Map<String, Double> computeAggregateForFormulas(
            Map<String, String> formulas,
            Map<String, Schema> schemasById,
            List<Trade> trades
    );

    Map<String, Double> computeAggregateForAstConfigs(
            Map<String, AstConfig> astConfigs,
            Map<String, Schema> schemasById,
            List<Trade> trades
    );
}
