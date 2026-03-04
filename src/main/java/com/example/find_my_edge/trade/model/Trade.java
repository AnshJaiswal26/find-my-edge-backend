package com.example.find_my_edge.trade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    private String id;

    private String externalId;

    private Long date;

    private Integer entryTime;
    private Integer exitTime;

    private String symbol;

    private String direction;

    private Integer qty;

    private Double entryPrice;
    private Double exitPrice;

    private Double charges;

    private Map<String, Object> values = new HashMap<>();
}