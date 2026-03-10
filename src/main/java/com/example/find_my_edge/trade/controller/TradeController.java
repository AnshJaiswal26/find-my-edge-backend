package com.example.find_my_edge.trade.controller;

import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.trade.dto.TradeValueUpdateRequestDto;
import com.example.find_my_edge.trade.mapper.TradeDtoMapper;
import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.trade.dto.TradeDto;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.service.TradeOrchestratorService;
import com.example.find_my_edge.trade.service.TradeService;

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

    private final TradeOrchestratorService orchestratorService;

    /* ---------------- GET ALL ---------------- */

    @GetMapping
    public ResponseEntity<ApiResponse<List<TradeDto>>> getAllTrades() {

        List<Trade> trades = tradeService.getAll();

        List<TradeDto> response = trades.stream()
                                        .map(tradeDtoMapper::toResponse)
                                        .toList();

        return buildResponse(response, "Trades fetched successfully");
    }

    /* ---------------- GET BY ID ---------------- */

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeDto>> getTradeById(@PathVariable String id) {

        Trade trade = tradeService.getById(id);

        return buildResponse(
                tradeDtoMapper.toResponse(trade),
                "Trade fetched successfully"
        );
    }

    /* ---------------- CREATE ---------------- */

    @PostMapping
    public ResponseEntity<ApiResponse<TradeDto>> createTrade(
            @RequestBody TradeDto request
    ) {

        Trade model = tradeDtoMapper.toModel(request);

        Trade saved = tradeService.create(model);

        return buildResponse(
                tradeDtoMapper.toResponse(saved),
                "Trade created successfully"
        );
    }

    /* ---------------- UPDATE ---------------- */

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeDto>> updateTrade(
            @PathVariable String id,
            @RequestBody TradeDto request
    ) {

        Trade model = tradeDtoMapper.toModel(request);

        Trade updated = tradeService.update(id, model);

        return buildResponse(
                tradeDtoMapper.toResponse(updated),
                "Trade updated successfully"
        );
    }

    @PatchMapping("/{id}/value")
    public ResponseEntity<ApiResponse<RecomputeResult>> updateTradeValue(
            @PathVariable String id,
            @RequestBody TradeValueUpdateRequestDto dto
    ) {

        RecomputeResult recomputeResult =
                orchestratorService.updateTradeValueAndRecomputed(
                        id,
                        dto.getField(),
                        dto.getValue()
                );

        return buildResponse(
                recomputeResult,
                "Trade updated successfully"
        );
    }

    /* ---------------- DELETE ---------------- */

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTrade(@PathVariable String id) {

        tradeService.delete(id);

        return buildResponse(null, "Trade deleted successfully");
    }


}