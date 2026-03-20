package com.example.find_my_edge.trade_setup.exception;

public class TradeSetupNotFoundException extends RuntimeException {
    public TradeSetupNotFoundException(String message) {
        super(message);
    }

    public TradeSetupNotFoundException() {
        super("Trade setup not found");
    }
}
