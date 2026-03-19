package com.example.find_my_edge.trade_setup.entity;

import com.example.find_my_edge.trade.entity.TradeEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
public class TradeSetupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String imageUrl;

    private Double targetProfitMin;
    private Double targetProfitMax;

    private Double riskReward;

    private Double riskPercent;
    private Double riskAmount;

    private String entryCandle;
    private String exitCandle;

    @Column(columnDefinition = "JSON")
    private List<String> indicatorsUsed;

    private UUID userId;

    @OneToMany(mappedBy = "tradeSetup")
    private List<TradeEntity> trades;
}