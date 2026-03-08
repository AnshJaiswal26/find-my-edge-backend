package com.example.find_my_edge.analytics.service;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import java.util.Collection;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ComputeService {

    <T> void executeAggregate(
            Collection<T> source,
            Function<T, String> keyFn,
            BiFunction<String, T, String> formulaFn,
            BiFunction<String, T, AstConfig> cfgFn,
            BiConsumer<String, Double> consumer,
            ComputationContext ctx
    );

    Map<String, Double> computeAggregatePerGroupByAstConfigs(
            GroupConfig groupConfig,
            AstConfig astConfig
    );

    Map<String, Double> computeAggregatePerGroupByFormula(
            GroupConfig groupConfig,
            String formula
    );
}
