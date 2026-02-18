package com.example.find_my_edge.core.schema.service.impl;

import com.example.find_my_edge.common.exceptions.SchemaDependencyException;
import com.example.find_my_edge.common.exceptions.SchemaNotFoundException;
import com.example.find_my_edge.common.exceptions.SchemaOperationNotAllowedException;
import com.example.find_my_edge.common.exceptions.SchemaValidationException;
import com.example.find_my_edge.core.schema.dto.SchemaRequest;
import com.example.find_my_edge.core.schema.enums.SchemaSource;
import com.example.find_my_edge.core.schema.service.SchemaService;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchemaServiceImpl implements SchemaService {

    // Temporary in-memory store (replace with DB later)
    private final Map<String, SchemaRequest> schemaStore = new HashMap<>();
    private final List<String> schemaOrder = new ArrayList<>();

    /* ---------------- CREATE ---------------- */
    @Override
    public Map<String, Object> create(SchemaRequest request) {

        String id = UUID.randomUUID().toString();
        request.setId(id);

        schemaStore.put(id, request);
        schemaOrder.add(id); // IMPORTANT

        return Map.of(
                "schema", request,
                "order", new ArrayList<>(schemaOrder)
        );
    }


    /* ---------------- UPDATE ---------------- */
    @Override
    public Map<String, Object> update(SchemaRequest request) {

        String id = request.getId();

        if (id == null || !schemaStore.containsKey(id)) {
            throw new SchemaNotFoundException("Schema not found");
        }

        schemaStore.put(id, request);

        return Map.of(
                "schema", request,
                "order", new ArrayList<>(schemaOrder)
        );
    }

    /* ---------------- GET BY ID ---------------- */
    @Override
    public SchemaRequest getById(String id) {
        SchemaRequest schema = schemaStore.get(id);

        if (schema == null) {
            throw new SchemaNotFoundException("Schema not found");
        }

        return schema;
    }

    /* ---------------- GET ALL ---------------- */
    @Override
    public Map<String, Object> getAll() {

        List<SchemaRequest> schemas = new ArrayList<>();
        Map<String, SchemaRequest> schemasById = new HashMap<>();

        for (String id : schemaOrder) {
            SchemaRequest schema = schemaStore.get(id);

            if (schema != null) {
                schemas.add(schema);              // for array use-case
                schemasById.put(id, schema);      // map (important)
            }
        }

        return Map.of(
                "schemas", schemas,
                "schemasById", schemasById,
                "order", new ArrayList<>(schemaOrder)
        );
    }


    /* ---------------- DELETE ---------------- */
    @Override
    public Map<String, Object> delete(String id) {

        if (!schemaStore.containsKey(id)) {
            throw new SchemaNotFoundException("Schema not found");
        }

        SchemaRequest schema = schemaStore.get(id);

        if (schema.getSource() == SchemaSource.SYSTEM) {
            throw new SchemaOperationNotAllowedException("System schema cannot be deleted: " + id);
        }


        List<String> dependents = new ArrayList<>();

        for (SchemaRequest s : schemaStore.values()) {
            if (s.getDependencies() != null && s.getDependencies().contains(id)) {
                dependents.add(s.getLabel());
            }
        }

        if (!dependents.isEmpty()) {
            throw new SchemaDependencyException(
                    "Cannot delete schema '" + schemaStore.get(id).getLabel() + "' because it is referenced by: " + String.join(", ", dependents)
            );
        }

        schemaStore.remove(id);
        schemaOrder.remove(id);

        return Map.of(
                "order", new ArrayList<>(schemaOrder)
        );
    }


    @Override
    public List<String> updateOrder(List<String> order) {

        if (order.size() != schemaStore.size()) {
            throw new SchemaValidationException("Invalid order size");
        }

        for (String id : order) {
            if (!schemaStore.containsKey(id)) {
                throw new SchemaNotFoundException("Invalid schema id in order: " + id);
            }
        }

        schemaOrder.clear();
        schemaOrder.addAll(order);

        return new ArrayList<>(schemaOrder);
    }

    @Override
    public void seed(SchemaRequest schema) {
        String id = schema.getId();

        if (!schemaStore.containsKey(id)) {
            schemaOrder.add(id);
        }
        schemaStore.put(id, schema);
    }

}
