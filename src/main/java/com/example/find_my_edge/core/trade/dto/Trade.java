package com.example.find_my_edge.core.trade.dto;

import lombok.Data;

@Data
public class Trade {
    private String id;
    private String date;
    private String entryTime;
    private String exitTime;
    private String symbol;

    private double entry;
    private double exit;
    private int qty;

    private long duration;
    private double pnl;
    private double cumulativePnl;
    private double capital;

    private double risk;
    private double riskReward;
    private double charges;

    @Override
    public String toString() {
        return "Trade{" +
               "tradeId='" + id + '\'' +
               ", date='" + date + '\'' +
               ", entryTime='" + entryTime + '\'' +
               ", exitTime='" + exitTime + '\'' +
               ", duration=" + duration +
               ", symbol='" + symbol + '\'' +
               ", entry=" + entry +
               ", exit=" + exit +
               ", qty=" + qty +
               ", pnl=" + pnl +
               ", cumulativePnl=" + cumulativePnl +
               ", capital=" + capital +
               ", risk=" + risk +
               ", riskReward=" + riskReward +
               ", charges=" + charges +
               '}';
    }
}
