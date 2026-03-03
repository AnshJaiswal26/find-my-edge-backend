package com.example.find_my_edge.trade_metrics.service.impl;

import com.example.find_my_edge.schema.enums.ViewType;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.trade_metrics.dto.TradeMetricTableData;
import com.example.find_my_edge.trade_metrics.service.TradeMetricService;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.enums.PageType;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeMetricServiceImpl implements TradeMetricService {

    private final WorkspaceService workspaceService;

    private final SchemaService schemaService;


    @Override
    public TradeMetricTableData init() {
        PageConfig page = workspaceService.getPage(PageType.TRADE_METRIC.key());

        List<String> order = schemaService.getOrder(ViewType.TABLE);

        TradeMetricTableData tradeMetricTableData = new TradeMetricTableData();
        tradeMetricTableData.setColumnWidths(page.getColumnWidths());

        tradeMetricTableData.setColumnsOrder(order);


        return tradeMetricTableData;
    }
}
