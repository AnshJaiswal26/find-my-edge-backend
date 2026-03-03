package com.example.find_my_edge.trade.controller;

import com.example.find_my_edge.trade.mapper.TradeDtoMapper;
import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.trade.dto.TradeRequestDto;
import com.example.find_my_edge.trade.dto.TradeResponseDto;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.service.TradeService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController extends BaseController {

    private final TradeService tradeService;
    private final TradeDtoMapper tradeDtoMapper;

    /* ---------------- GET ALL ---------------- */

    @GetMapping
    public ResponseEntity<ApiResponse<List<TradeResponseDto>>> getAllTrades() {

        List<Trade> trades = tradeService.getAll();

        List<TradeResponseDto> response = trades.stream()
                                                .map(tradeDtoMapper::toResponse)
                                                .toList();

        return buildResponse(response, "Trades fetched successfully");
    }

    /* ---------------- GET BY ID ---------------- */

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeResponseDto>> getTradeById(@PathVariable String id) {

        Trade trade = tradeService.getById(id);

        return buildResponse(tradeDtoMapper.toResponse(trade), "Trade fetched successfully");
    }

    /* ---------------- CREATE ---------------- */

    @PostMapping
    public ResponseEntity<ApiResponse<TradeResponseDto>> createTrade(
            @RequestBody TradeRequestDto request
    ) {

        Trade model = tradeDtoMapper.toDomain(request);

        Trade saved = tradeService.create(model);

        return buildResponse(tradeDtoMapper.toResponse(saved), "Trade created successfully");
    }

    /* ---------------- UPDATE ---------------- */

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeResponseDto>> updateTrade(
            @PathVariable String id,
            @RequestBody TradeRequestDto request
    ) {

        Trade model = tradeDtoMapper.toDomain(request);

        Trade updated = tradeService.update(id, model);

        return buildResponse(tradeDtoMapper.toResponse(updated), "Trade updated successfully");
    }

    /* ---------------- DELETE ---------------- */

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTrade(@PathVariable String id) {

        tradeService.delete(id);

        return buildResponse(null, "Trade deleted successfully");
    }

    @PostMapping("/sync/full")
    public ResponseEntity<ApiResponse<List<TradeResponseDto>>> fullSync() {

        List<Trade> trades = tradeService.fetchAllAndSave();

        List<TradeResponseDto> response = trades.stream()
                                                .map(tradeDtoMapper::toResponse)
                                                .toList();

        return buildResponse(response, "Full sync completed");
    }

    @PostMapping("/sync/incremental")
    public ResponseEntity<ApiResponse<List<TradeResponseDto>>> incrementalSync() {

        List<Trade> trades = tradeService.fetchIncrementalAndSave();

        List<TradeResponseDto> response = trades.stream()
                                                .map(tradeDtoMapper::toResponse)
                                                .toList();

        return buildResponse(response, "Incremental sync completed");
    }

    @GetMapping("/sync/custom")
    public ResponseEntity<ApiResponse<List<TradeResponseDto>>> customSync(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {

        List<Trade> trades = tradeService.fetchCustomAndSave(fromDate, toDate);

        List<TradeResponseDto> response = trades.stream()
                                                .map(tradeDtoMapper::toResponse)
                                                .toList();

        return buildResponse(response, "Custom sync completed");
    }
}