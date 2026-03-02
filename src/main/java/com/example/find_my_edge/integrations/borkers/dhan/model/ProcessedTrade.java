package com.example.find_my_edge.integrations.borkers.dhan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedTrade {

    private String orderId;
    private String date;
    private String entryTime;
    private String exitTime;

    private String symbol;
    private Double strikePrice;

    private String direction;

    private int quantity;

    private double buyPrice;
    private double sellPrice;

    private double pnl;

    private double charges;
}