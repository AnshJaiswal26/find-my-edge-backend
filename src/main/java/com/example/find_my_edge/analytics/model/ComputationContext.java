package com.example.find_my_edge.analytics.model;

import com.example.find_my_edge.schema.model.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ComputationContext {

    private final Map<String, Map<String, Object>> raw;
    private final Map<String, Map<String, Object>> computed;
    private final List<String> tradeOrder;

    private final Map<String, Schema> schemasById;
    private final List<String> schemaOrder;

}