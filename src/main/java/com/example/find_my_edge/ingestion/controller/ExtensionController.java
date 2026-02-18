package com.example.find_my_edge.ingestion.controller;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.service.TradeFieldService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/extension")
@RequiredArgsConstructor
public class ExtensionController {

    private final TradeFieldService tradeFieldService;

    /* ---------------- GET ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getTradeRecords() {

        List<?> records = tradeFieldService.getTradeRecords();

        return buildResponse(records, "Trade Records fetched successfully");
    }

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> addTradeFields(
            @RequestBody List<FieldDataRequest> request
    ) {

        Object saved = tradeFieldService.addTradeRecords(request);

        return buildResponse(saved, "Trade Record added successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<Object>> updateTradeRecords(
            @PathVariable Long tradeId,
            @RequestBody List<FieldDataRequest> request
    ) {

        Object updated = tradeFieldService.updateTradeRecords(tradeId, request);

        return buildResponse(updated, "Trade Record updated successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<Object>> deleteTradeRecords(
            @PathVariable Long tradeId
    ) {

        tradeFieldService.deleteTradeRecord(tradeId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .state(ResponseState.SUCCESS)
                           .httpStatus(HttpStatus.OK.value())
                           .message("Trade Record deleted successfully")
                           .data(null)
                           .meta(Map.of("empty", true, "count", 0))
                           .build()
        );
    }

    /* ---------------- COMMON BUILDER ---------------- */
    private ResponseEntity<ApiResponse<Object>> buildResponse(Object data, String message) {

        boolean isList = data instanceof List;
        int count = isList ? ((List<?>) data).size() : 1;
        boolean empty = data == null || (isList && ((List<?>) data).isEmpty());

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .state(ResponseState.SUCCESS)
                           .httpStatus(HttpStatus.OK.value())
                           .message(message)
                           .data(data)
                           .meta(Map.of("empty", empty, "count", count))
                           .build()
        );
    }
}
