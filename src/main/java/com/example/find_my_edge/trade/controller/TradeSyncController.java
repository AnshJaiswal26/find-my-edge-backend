package com.example.find_my_edge.trade.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.trade.dto.TradeDto;
import com.example.find_my_edge.trade.service.TradeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/trades/{broker}/sync")
@RequiredArgsConstructor
public class TradeSyncController extends BaseController {

    private final TradeSyncService tradeSyncService;

    @PostMapping("/full")
    public ResponseEntity<ApiResponse<List<TradeDto>>> fullSync(@PathVariable String broker) {

        tradeSyncService.fullSync(broker);

        return buildResponse(null, "Full sync completed");
    }

    @PostMapping("/incremental")
    public ResponseEntity<ApiResponse<List<TradeDto>>> incrementalSync(@PathVariable String broker) {

        tradeSyncService.incrementalSync(broker);

        return buildResponse(null, "Incremental sync completed");
    }

    @PostMapping("/custom")
    public ResponseEntity<ApiResponse<List<TradeDto>>> customSync(
            @PathVariable String broker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {

        tradeSyncService.customSync(broker, fromDate, toDate);

        return buildResponse(null, "Custom sync completed");
    }
}
