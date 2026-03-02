package com.example.find_my_edge.schema.service;

import com.example.find_my_edge.schema.entity.SchemaOverrideEntity;
import com.example.find_my_edge.schema.model.Schema;

import java.util.List;

public interface SchemaOverrideService {
    List<Schema> applyOverrides(List<Schema> systems, String userId);

    Schema applySingleOverride(Schema system, SchemaOverrideEntity override);

    Schema applyOverride(Schema system, String userId);

    SchemaOverrideEntity getOrExisting(String schemaId, String userId);

    SchemaOverrideEntity save(SchemaOverrideEntity schemaOverrideEntity);
}
