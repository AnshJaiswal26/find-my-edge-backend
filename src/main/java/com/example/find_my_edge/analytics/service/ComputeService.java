package com.example.find_my_edge.analytics.service;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;

import java.util.Map;

public interface ComputeService {

    Map<String, Double> computeAggregatePerGroupByAstConfigs(
            GroupConfig groupConfig,
            AstConfig astConfig
    );

    Map<String, Double> computeAggregatePerGroupByFormula(
            GroupConfig groupConfig,
            String formula
    );
}
