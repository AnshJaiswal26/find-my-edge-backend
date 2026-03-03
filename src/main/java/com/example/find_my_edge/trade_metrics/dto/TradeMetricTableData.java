package com.example.find_my_edge.trade_metrics.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TradeMetricTableData {
    private List<String> columnsOrder;
    private Map<String, Integer> columnWidths;
}
