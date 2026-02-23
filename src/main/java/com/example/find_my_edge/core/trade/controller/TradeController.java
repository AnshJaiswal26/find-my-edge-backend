package com.example.find_my_edge.core.trade.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.trade.dto.TradeRequestDTO;
import com.example.find_my_edge.core.trade.dto.TradeResponseDTO;
import com.example.find_my_edge.core.trade.service.TradeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController extends BaseController {

    private final TradeService tradeService;

    /* ---------------- GET ALL ---------------- */

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllTrades() {
        List<TradeResponseDTO> trades = tradeService.getAll();
        return buildResponse(trades, "Trades fetched successfully");
    }

    /* ---------------- GET BY ID ---------------- */

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getTradeById(@PathVariable String id) {
        TradeResponseDTO trade = tradeService.getById(id);
        return buildResponse(trade, "Trade fetched successfully");
    }

    /* ---------------- CREATE ---------------- */

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createTrade(
            @RequestBody TradeRequestDTO request
    ) {
        TradeResponseDTO saved = tradeService.create(request);
        return buildResponse(saved, "Trade created successfully");
    }

    /* ---------------- UPDATE ---------------- */

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateTrade(
            @PathVariable String id,
            @RequestBody TradeRequestDTO request
    ) {
        TradeResponseDTO updated = tradeService.update(id, request);
        return buildResponse(updated, "Trade updated successfully");
    }

    /* ---------------- DELETE ---------------- */

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTrade(@PathVariable String id) {
        tradeService.delete(id);
        return buildResponse(null, "Trade deleted successfully");
    }
}