package com.example.find_my_edge.api.schema.dto;

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
public class SchemaResponseDTOBundle {
    private List<SchemaResponseDto> schemas = new ArrayList<>();
    private Map<String, SchemaResponseDto> schemasById = new HashMap<>();
    private List<String> schemasOrder = new ArrayList<>();
}
