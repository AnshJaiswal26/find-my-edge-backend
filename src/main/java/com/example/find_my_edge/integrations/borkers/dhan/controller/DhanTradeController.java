package com.example.find_my_edge.integrations.borkers.dhan.controller;

import com.example.find_my_edge.integrations.borkers.dhan.service.DhanTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dhan/trades")
@RequiredArgsConstructor
public class DhanTradeController {

    private final DhanTradeService tradeService;

    @GetMapping
    public Object getTrades(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam(defaultValue = "0") int page
    ) {
        return tradeService.getTrades(fromDate, toDate, page);
    }
}