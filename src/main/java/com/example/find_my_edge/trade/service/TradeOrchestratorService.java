package com.example.find_my_edge.trade.service;

import com.example.find_my_edge.analytics.model.RecomputeResult;

public interface TradeOrchestratorService {
    RecomputeResult updateTradeValueAndRecomputed(
            String field,
            String tradeId,
            Object value
    );
}
