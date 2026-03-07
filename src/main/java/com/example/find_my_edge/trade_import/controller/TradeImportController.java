package com.example.find_my_edge.trade_import.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.trade_import.dto.FieldDataRequestDto;
import com.example.find_my_edge.trade_import.dto.ImportedTradeResponseDto;
import com.example.find_my_edge.trade_import.service.TradeImportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trade-import")
@RequiredArgsConstructor
public class TradeImportController extends BaseController {

    private final TradeImportService tradeImportService;

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ImportedTradeResponseDto>>> getImportedTrades() {

        List<ImportedTradeResponseDto> all = tradeImportService.getAll();

        return buildResponse(all, "Imported Trades fetched successfully");
    }

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<ImportedTradeResponseDto>> createImportedTrades(
            @RequestBody List<FieldDataRequestDto> request
    ) {

        ImportedTradeResponseDto responseDto =
                tradeImportService.create(request);

        return buildResponse(
                responseDto,
                "Imported Trade added successfully"
        );
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{importId}")
    public ResponseEntity<ApiResponse<ImportedTradeResponseDto>> updateImportedTrade(
            @PathVariable Long importId,
            @RequestBody List<FieldDataRequestDto> request
    ) {

        ImportedTradeResponseDto response = tradeImportService.update(importId, request);

        return buildResponse(
                response,
                "Imported Trade updated successfully"
        );
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{importId}")
    public ResponseEntity<ApiResponse<Object>> deleteImportedTrade(
            @PathVariable Long importId
    ) {

        tradeImportService.delete(importId);

        return buildResponse(null, "Imported Trade deleted successfully");
    }

}
