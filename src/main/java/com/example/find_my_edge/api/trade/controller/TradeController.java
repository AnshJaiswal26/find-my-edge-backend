package com.example.find_my_edge.api.trade.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.api.trade.dto.TradeRequestDTO;
import com.example.find_my_edge.api.trade.dto.TradeResponseDTO;
import com.example.find_my_edge.api.trade.mapper.TradeDTOMapper;
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
    private final TradeDTOMapper tradeDTOMapper;

    /* ---------------- GET ALL ---------------- */

    @GetMapping
    public ResponseEntity<ApiResponse<List<TradeResponseDTO>>> getAllTrades() {

        List<Trade> trades = tradeService.getAll();

        List<TradeResponseDTO> response = trades.stream()
                                                .map(tradeDTOMapper::toDTO)
                                                .toList();

        return buildResponse(response, "Trades fetched successfully");
    }

    /* ---------------- GET BY ID ---------------- */

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeResponseDTO>> getTradeById(@PathVariable String id) {

        Trade trade = tradeService.getById(id);

        return buildResponse(tradeDTOMapper.toDTO(trade), "Trade fetched successfully");
    }

    /* ---------------- CREATE ---------------- */

    @PostMapping
    public ResponseEntity<ApiResponse<TradeResponseDTO>> createTrade(
            @RequestBody TradeRequestDTO request
    ) {

        Trade model = tradeDTOMapper.toModel(request);

        Trade saved = tradeService.create(model);

        return buildResponse(tradeDTOMapper.toDTO(saved), "Trade created successfully");
    }

    /* ---------------- UPDATE ---------------- */

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeResponseDTO>> updateTrade(
            @PathVariable String id,
            @RequestBody TradeRequestDTO request
    ) {

        Trade model = tradeDTOMapper.toModel(request);

        Trade updated = tradeService.update(id, model);

        return buildResponse(tradeDTOMapper.toDTO(updated), "Trade updated successfully");
    }

    /* ---------------- DELETE ---------------- */

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTrade(@PathVariable String id) {

        tradeService.delete(id);

        return buildResponse(null, "Trade deleted successfully");
    }
}