package com.example.find_my_edge.core.backtest.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TradeField {

    public TradeField() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long tradeId;

    private String label;
    private String type;
    private String value;
    private String mappedWith;

    @Column(columnDefinition = "JSON")
    private String options;

    public TradeField(Long tradeId, String label, String type, String value, String mappedWith, String options) {
        this.tradeId = tradeId;
        this.label = label;
        this.type = type;
        this.value = value;
        this.mappedWith = mappedWith;
        this.options = options;
    }

}
