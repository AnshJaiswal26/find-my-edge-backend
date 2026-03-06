package com.example.find_my_edge.schema.service;

import com.example.find_my_edge.schema.entity.SchemaOverrideEntity;
import com.example.find_my_edge.schema.model.Schema;

import java.util.List;
import java.util.UUID;

public interface SchemaOverrideService {
    List<Schema> applyOverrides(List<Schema> systems, UUID userId);

    Schema applySingleOverride(Schema system, SchemaOverrideEntity override);

    Schema applyOverride(Schema system, UUID userId);

    SchemaOverrideEntity getOrExisting(String schemaId, UUID userId);

    SchemaOverrideEntity save(SchemaOverrideEntity schemaOverrideEntity);
}
