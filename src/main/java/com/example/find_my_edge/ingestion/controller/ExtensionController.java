package com.example.find_my_edge.ingestion.controller;

import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.dto.TradeRecordsResponse;
import com.example.find_my_edge.core.backtest.service.TradeFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extension")
@RequiredArgsConstructor
public class ExtensionController {

    private final TradeFieldService tradeFieldService;


    @GetMapping
    public ResponseEntity<List<TradeRecordsResponse>> getTradeRecords() {
        List<TradeRecordsResponse> records = tradeFieldService.getTradeRecords();
        return new ResponseEntity<>(
                records,
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<TradeRecordsResponse> addTradeFields(@RequestBody List<FieldDataRequest> request) {
        TradeRecordsResponse records = tradeFieldService.addTradeRecords(request);
        return new ResponseEntity<>(
                records,
                HttpStatus.OK
        );
    }

    @PutMapping("/{tradeId}")
    public ResponseEntity<TradeRecordsResponse> updateTradeRecords(@PathVariable Long tradeId, @RequestBody List<FieldDataRequest> request) {
        TradeRecordsResponse records = tradeFieldService.updateTradeRecords(
                tradeId,
                request
        );
        return new ResponseEntity<>(
                records,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{tradeId}")
    public ResponseEntity<?> deleteTradeRecords(@PathVariable Long tradeId) {
        return tradeFieldService.deleteTradeRecord(tradeId);
    }
}
