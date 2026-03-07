package com.example.find_my_edge.analytics.service;

import com.example.find_my_edge.analytics.model.TradeContextSplit;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.trade.model.Trade;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ComputeService {

    <T> void executeAggregate(
            Map<String, T> source,
            Function<String, String> formulaFn,
            Function<String, AstConfig> cfgFn,
            BiConsumer<String, Double> consumer,
            Map<String, Schema> schemasById,
            List<Trade> trades
    );

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
