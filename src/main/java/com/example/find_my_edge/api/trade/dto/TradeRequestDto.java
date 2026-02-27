package com.example.find_my_edge.api.trade.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TradeRequestDto {

    private Map<String, String> values = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> getValues() {
        return values;
    }

    @JsonAnySetter
    public void setValue(String key, String value) {
        values.put(key, value);
    }
}