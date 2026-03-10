package com.example.find_my_edge.schema.service.impl;

import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.service.RecomputeService;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaUpdate;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.workspace.enums.PageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SchemaOrchestratorServiceImpl implements SchemaOrchestratorService{

    private final SchemaService schemaService;
    private final RecomputeService recomputeService;

    @Override
    public SchemaUpdate updateSchemaAndRecompute(String schemaId, Schema schema) {

        Schema saved = schemaService.update(schemaId, schema);

        boolean formulaChanged = !Objects.equals(schema.getIdFormula(), saved.getFormula());

        RecomputeResult recomputeResult = null;

        if (formulaChanged) {
            recomputeResult =
                    recomputeService.recomputeOnDefinitionChange(
                            PageType.DASHBOARD.key(),
                            schemaId
                    );
        }

        return new SchemaUpdate(schema, recomputeResult);
    }
}