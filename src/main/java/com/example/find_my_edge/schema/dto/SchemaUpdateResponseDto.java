package com.example.find_my_edge.schema.dto;

import com.example.find_my_edge.analytics.model.RecomputeResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaUpdateResponseDto {
    private SchemaResponseDto schema;
    private RecomputeResult recomputeResult;
}
