package com.example.find_my_edge.core.trade.controller;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.trade.dto.Trade;
import com.example.find_my_edge.core.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getTrades() {
        List<Trade> trades = tradeService.getAll();
        return buildResponse(trades, "Trades fetched successfully");
    }

    /* ---------------- GET BY ID ---------------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getTradeById(@PathVariable String id) {
        Trade trade = tradeService.getById(id);
        return buildResponse(trade, "Trade fetched successfully");
    }

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createTrade(@RequestBody Trade trade) {
        Trade saved = tradeService.save(trade);
        return buildResponse(saved, "Trade created successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateTrade(
            @PathVariable String id,
            @RequestBody Trade trade
    ) {
        trade.setId(id);
        Trade updated = tradeService.save(trade);
        return buildResponse(updated, "Trade updated successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTrade(@PathVariable String id) {
        tradeService.delete(id);
        return buildResponse(null, "Trade deleted successfully");
    }

    /* ---------------- COMMON BUILDER ---------------- */
    private ResponseEntity<ApiResponse<Object>> buildResponse(Object data, String message) {

        boolean isList = data instanceof List;
        int count = isList ? ((List<?>) data).size() : (data == null ? 0 : 1);
        boolean empty = data == null || (isList && ((List<?>) data).isEmpty());

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .state(ResponseState.SUCCESS)
                           .httpStatus(HttpStatus.OK.value())
                           .message(message)
                           .data(data)
                           .meta(Map.of(
                                   "empty", empty,
                                   "count", count
                           ))
                           .build()
        );
    }
}
