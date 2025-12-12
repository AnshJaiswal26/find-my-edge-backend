package com.example.find_my_edge.core.backtest.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TradeField {

    public TradeField() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "trade_id")
    private Trade trade;

    private String label;
    private String type;
    private String value;
    private String mappedWith;
    private int mappedColumn;


    @Column(columnDefinition = "JSON")
    private String options;

    public TradeField(String label, String type, String value, String mappedWith, int mappedColumn, String options) {
        this.label = label;
        this.type = type;
        this.value = value;
        this.mappedWith = mappedWith;
        this.mappedColumn = mappedColumn;
        this.options = options;
    }

}
