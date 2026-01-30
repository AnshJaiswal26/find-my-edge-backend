package com.example.find_my_edge.core.trade.controller;

import com.example.find_my_edge.core.trade.dto.Trade;
import com.example.find_my_edge.core.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public ResponseEntity<List<Trade>> getTrades() {
        return new ResponseEntity<>(tradeService.getTrades(), HttpStatus.OK);
    }
}
