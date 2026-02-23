package com.example.find_my_edge.core.trade_import.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.trade_import.dto.FieldDataRequestDTO;
import com.example.find_my_edge.core.trade_import.dto.ImportedTradeResponseDTO;
import com.example.find_my_edge.core.trade_import.service.impl.TradeImportServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trade-import")
@RequiredArgsConstructor
public class TradeImportController extends BaseController {

    private final TradeImportServiceImpl tradeImportServiceImpl;

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getImportedTrades() {

        List<ImportedTradeResponseDTO> records = tradeImportServiceImpl.getAll();

        return buildResponse(records, "Imported Trades fetched successfully");
    }

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createImportedTrades(
            @RequestBody List<FieldDataRequestDTO> request
    ) {

        Object saved = tradeImportServiceImpl.create(request);

        return buildResponse(saved, "Imported Trade added successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{importId}")
    public ResponseEntity<ApiResponse<Object>> updateImportedTrade(
            @PathVariable Long importId,
            @RequestBody List<FieldDataRequestDTO> request
    ) {

        Object updated = tradeImportServiceImpl.update(importId, request);

        return buildResponse(updated, "Imported Trade updated successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{importId}")
    public ResponseEntity<ApiResponse<Object>> deleteImportedTrade(
            @PathVariable Long importId
    ) {

        tradeImportServiceImpl.delete(importId);

        return buildResponse(null, "Imported Trade deleted successfully");
    }

}
