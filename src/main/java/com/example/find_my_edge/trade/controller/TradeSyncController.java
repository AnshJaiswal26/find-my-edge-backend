package com.example.find_my_edge.trade.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.trade.dto.TradeSyncStatusResponse;
import com.example.find_my_edge.trade.service.TradeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/trades/{broker}/sync")
@RequiredArgsConstructor
public class TradeSyncController extends BaseController {

    private final TradeSyncService tradeSyncService;

    @PostMapping("/full")
    public ResponseEntity<ApiResponse<Void>> fullSync(
            @PathVariable Broker broker
    ) {

        tradeSyncService.fullSync(broker);

        return buildResponse(null, "Full sync completed");
    }

    @PostMapping("/incremental")
    public ResponseEntity<ApiResponse<Void>> incrementalSync(
            @PathVariable Broker broker
    ) {

        tradeSyncService.incrementalSync(broker);

        return buildResponse(null, "Incremental sync completed");
    }

    @PostMapping("/custom")
    public ResponseEntity<ApiResponse<Void>> customSync(
            @PathVariable Broker broker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {

        tradeSyncService.customSync(broker, fromDate, toDate);

        return buildResponse(null, "Custom sync completed");
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<TradeSyncStatusResponse>> syncStatus(
            @PathVariable Broker broker
    ) {

        TradeSyncStatusResponse tradeSyncStatus = tradeSyncService.getTradeSyncStatus(broker);

        return buildResponse(tradeSyncStatus, "Custom sync completed");
    }
}
