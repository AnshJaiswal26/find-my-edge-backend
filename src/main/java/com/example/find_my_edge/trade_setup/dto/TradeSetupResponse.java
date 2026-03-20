package com.example.find_my_edge.trade_setup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSetupResponse {

    private String id;
    private String name;
    private String imageUrl;
    private String imagePublicId;

    private Double targetProfitMin;
    private Double targetProfitMax;

    private Double riskReward;
    private Double riskPercent;
    private Double riskAmount;

    private String entryCandle;
    private String exitCandle;

    private List<String> indicatorsUsed;

    private int totalTrades;
}