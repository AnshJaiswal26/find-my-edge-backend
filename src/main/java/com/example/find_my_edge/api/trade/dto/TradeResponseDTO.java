package com.example.find_my_edge.api.trade.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TradeResponseDTO {

    private String id;

    private Map<String, Object> values = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getValues() {
        return values;
    }
}