package com.example.find_my_edge.core.trade.service;

import com.example.find_my_edge.core.trade.dto.Trade;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TradeService {

    private final Map<String, Trade> tradeStore = new LinkedHashMap<>();

    /* ---------------- READ ---------------- */

    public List<Trade> getAll() {
        return new ArrayList<>(tradeStore.values());
    }

    public Trade getById(String id) {
        return tradeStore.get(id);
    }

    /* ---------------- WRITE ---------------- */

    public void saveAll(List<Trade> trades) {
        trades.forEach(trade -> tradeStore.put(trade.getId(), trade));
    }

    public Trade save(Trade trade) {
        System.out.println(trade);
        tradeStore.put(trade.getId(), trade);
        return trade;
    }

    public void delete(String id) {
        tradeStore.remove(id);
    }

    /* ---------------- UTIL ---------------- */

    public boolean exists(String id) {
        return tradeStore.containsKey(id);
    }
}
