package com.example.find_my_edge.api.trade_import.controller;

import com.example.find_my_edge.api.trade_import.mapper.ImportedTradeFieldMapper;
import com.example.find_my_edge.api.trade_import.mapper.ImportedTradeMapper;
import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.api.trade_import.dto.FieldDataRequestDTO;
import com.example.find_my_edge.api.trade_import.dto.ImportedTradeResponseDTO;
import com.example.find_my_edge.trade_import.entity.ImportedTradeEntity;
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
    private final ImportedTradeMapper importedTradeMapper;
    private final ImportedTradeFieldMapper fieldMapper;

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ImportedTradeResponseDTO>>> getImportedTrades() {

        List<ImportedTradeEntity> all = tradeImportService.getAll();

        return buildResponse(
                all.stream()
                   .map(importedTradeMapper::toResponseDTO)
                   .toList(),
                "Imported Trades fetched successfully"
        );
    }

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<ImportedTradeResponseDTO>> createImportedTrades(
            @RequestBody List<FieldDataRequestDTO> request
    ) {

        ImportedTradeEntity importedTradeEntity =
                tradeImportService.create(request.stream()
                                                     .map(fieldMapper::toEntity).toList());

        return buildResponse(
                importedTradeMapper.toResponseDTO(importedTradeEntity),
                "Imported Trade added successfully"
        );
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{importId}")
    public ResponseEntity<ApiResponse<ImportedTradeResponseDTO>> updateImportedTrade(
            @PathVariable Long importId,
            @RequestBody List<FieldDataRequestDTO> request
    ) {

        ImportedTradeEntity update = tradeImportService.update(
                importId, request.stream()
                                 .map(fieldMapper::toEntity).toList()
        );

        return buildResponse(
                importedTradeMapper.toResponseDTO(update),
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
