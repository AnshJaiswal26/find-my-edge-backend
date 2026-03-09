package com.example.find_my_edge.schema.model;

import com.example.find_my_edge.analytics.model.RecomputeResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaUpdate {

    private Schema schema;
    private RecomputeResult recomputeResult;
}
