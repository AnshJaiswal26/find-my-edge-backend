package com.example.find_my_edge.trade_setup.dto;

import lombok.Data;

import java.util.List;

@Data
public class TradeSetupRequest {

    private String name;
    private String imageUrl;

    private Double targetProfitMin;
    private Double targetProfitMax;

    private Double riskReward;
    private Double riskPercent;
    private Double riskAmount;

    private String entryCandle;
    private String exitCandle;

    private List<String> indicatorsUsed;
}