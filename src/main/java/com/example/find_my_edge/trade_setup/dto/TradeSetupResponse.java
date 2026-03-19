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

    private Double riskReward;

    private List<String> indicatorsUsed;

    private int totalTrades;
}