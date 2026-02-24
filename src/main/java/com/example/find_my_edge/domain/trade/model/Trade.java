package com.example.find_my_edge.domain.trade.model;

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

    // dynamic fields (based on schema)
    private Map<String, String> values = new HashMap<>();
}