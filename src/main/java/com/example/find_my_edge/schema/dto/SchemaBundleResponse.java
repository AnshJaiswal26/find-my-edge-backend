package com.example.find_my_edge.schema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaBundleResponse {
    private List<SchemaResponse> schemas = new ArrayList<>();
    private Map<String, SchemaResponse> schemasById = new HashMap<>();
    private List<String> schemasOrder = new ArrayList<>();
}
