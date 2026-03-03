package com.example.find_my_edge.trade_metrics.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.trade_metrics.dto.TradeMetricTableData;
import com.example.find_my_edge.trade_metrics.service.TradeMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade-metric/init")
@RequiredArgsConstructor
public class TradeMetricController extends BaseController {

    private final TradeMetricService tradeMetricService;

    @GetMapping
    public ResponseEntity<TradeMetricTableData> getTradeMetric() {
        TradeMetricTableData response = tradeMetricService.init();

        return ResponseEntity.ok(response);
    }
}
