package com.example.find_my_edge.bootstrap.dto;

import com.example.find_my_edge.schema.dto.SchemaResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class BootstrapResponse {

    private Map<String, SchemaResponse> schemasById;

    private List<String> schemasOrder;

    private Map<String, Map<String, Object>> tradesById;

    private Map<String, Map<String, Object>> derivedByTradeId;

    private List<String> tradesOrder;
}
