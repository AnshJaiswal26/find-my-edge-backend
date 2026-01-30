package com.example.find_my_edge.core.backtest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeField {

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

}
