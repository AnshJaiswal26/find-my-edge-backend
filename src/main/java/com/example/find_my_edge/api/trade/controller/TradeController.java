package com.example.find_my_edge.api.trade.controller;

import com.example.find_my_edge.api.trade.mapper.TradeDtoMapper;
import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.api.trade.dto.TradeRequestDto;
import com.example.find_my_edge.api.trade.dto.TradeResponseDto;
import com.example.find_my_edge.domain.trade.model.Trade;
import com.example.find_my_edge.domain.trade.service.TradeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}