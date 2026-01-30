package com.example.find_my_edge.core.trade.dto;

import lombok.Data;

@Data
public class Trade {
    private String tradeId;
    private String date;
    private String entryTime;
    private String exitTime;
    private long duration;
    private String symbol;

    private double entry;
    private double exit;
    private int qty;

    private double pnl;
    private double cumulativePnl;
    private double capital;

    private double risk;
    private double rr;
    private double charges;
}
