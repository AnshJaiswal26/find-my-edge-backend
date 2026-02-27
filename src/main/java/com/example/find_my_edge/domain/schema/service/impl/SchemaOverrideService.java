package com.example.find_my_edge.domain.schema.service.impl;

import com.example.find_my_edge.domain.schema.entity.SchemaOverrideEntity;
import com.example.find_my_edge.domain.schema.model.Schema;

import java.util.List;

public interface SchemaOverrideService {
    List<Schema> applyOverrides(List<Schema> systems, String userId);

    Schema applySingleOverride(Schema system, SchemaOverrideEntity override);

    Schema applyOverride(Schema system, String userId);
}
