package com.example.find_my_edge.trade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TradeBundle {

    private List<String> tradeOrder;

    private Map<String, Trade> tradesById;
}
