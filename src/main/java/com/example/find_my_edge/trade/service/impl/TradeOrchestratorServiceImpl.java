package com.example.find_my_edge.trade.service.impl;

import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.service.RecomputeService;
import com.example.find_my_edge.trade.service.TradeOrchestratorService;
import com.example.find_my_edge.trade.service.TradeService;
import com.example.find_my_edge.workspace.enums.PageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeOrchestratorServiceImpl implements TradeOrchestratorService {

    private final RecomputeService recomputeService;
    private final TradeService tradeService;

    @Override
    public RecomputeResult updateTradeValueAndRecomputed(
            String field,
            String tradeId,
            Object value
    ) {

        tradeService.updateValue(tradeId, field, value);

        return recomputeService.recomputeByTradeField(
                PageType.DASHBOARD.key(),
                field,
                tradeId
        );
    }
}
