package com.example.find_my_edge.ingestion.controller;

import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.service.TradeFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extension")
@RequiredArgsConstructor
public class ExtensionController {

    private final TradeFieldService tradeFieldService;


    @GetMapping
    public ResponseEntity<ApiResponse<?>> getTradeRecords() {
        return tradeFieldService.getTradeRecords();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> addTradeFields(@RequestBody List<FieldDataRequest> request) {
        return tradeFieldService.addTradeRecords(request);
    }

    @PutMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<?>> updateTradeRecords(@PathVariable Long tradeId, @RequestBody List<FieldDataRequest> request) {
        return tradeFieldService.updateTradeRecords(
                tradeId,
                request
        );
    }

    @DeleteMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<?>> deleteTradeRecords(@PathVariable Long tradeId) {
        return tradeFieldService.deleteTradeRecord(tradeId);
    }
}
