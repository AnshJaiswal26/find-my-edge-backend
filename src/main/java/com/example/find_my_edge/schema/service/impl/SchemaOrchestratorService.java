package com.example.find_my_edge.schema.service.impl;

import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaUpdate;

public interface SchemaOrchestratorService {
    SchemaUpdate createSchemaAndRecompute(Schema schema);

    SchemaUpdate updateSchemaAndRecompute(String schemaId, Schema schema);
}
