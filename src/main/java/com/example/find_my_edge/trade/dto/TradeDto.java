package com.example.find_my_edge.trade.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TradeDto {

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

    @JsonAnyGetter
    public Map<String, Object> getValues() {
        return values;
    }
}