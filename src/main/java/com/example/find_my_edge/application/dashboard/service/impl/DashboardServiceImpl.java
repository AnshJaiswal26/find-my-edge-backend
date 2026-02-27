package com.example.find_my_edge.application.dashboard.service.impl;

import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.application.dashboard.model.DashboardData;
import com.example.find_my_edge.application.dashboard.service.DashboardService;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import com.example.find_my_edge.domain.schema.service.SchemaService;
import com.example.find_my_edge.domain.trade.model.Trade;
import com.example.find_my_edge.domain.trade.service.TradeService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final WorkspaceService workspaceService;
    private final ComputeService computeService;

    private final SchemaService schemaService;
    private final TradeService tradeService;

    @Override
    public DashboardData init() {

        SchemaBundle schemaBundle = schemaService.getAll();
        List<Trade> trades = tradeService.getAll();


        Map<String, Map<String, Double>> tradeContext =
                computeService.getTradeContext(schemaBundle.getSchemasById(), trades);


        return null;
    }
}
