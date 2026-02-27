package com.example.find_my_edge.domain.trade.exception;

public class TradeNotFoundException extends TradeException {
    public TradeNotFoundException(String id) {
        super("Trade not found: " + id);
    }
}
