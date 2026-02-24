package com.example.find_my_edge.domain.schema.model;

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
public class SchemaBundle {
    private List<Schema> schemas = new ArrayList<>();
    private Map<String, Schema> schemasById = new HashMap<>();
    private List<String> schemasOrder = new ArrayList<>();
}
