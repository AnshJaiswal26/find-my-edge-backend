package com.example.find_my_edge.analytics.service;

import com.example.find_my_edge.analytics.model.RecomputeResult;

public interface RecomputeService {
    RecomputeResult recomputeOnDefinitionChange(String pageName, String changedMetricId);

    RecomputeResult recomputeByTradeField(
            String pageName,
            String changedField,
            String changedTradeId
    );
}
