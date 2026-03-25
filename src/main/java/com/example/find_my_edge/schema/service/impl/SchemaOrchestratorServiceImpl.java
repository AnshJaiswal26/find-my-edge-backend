package com.example.find_my_edge.schema.service.impl;

import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.analytics.service.RecomputeService;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaUpdate;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.workspace.enums.PageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchemaOrchestratorServiceImpl implements SchemaOrchestratorService {

    private final SchemaService schemaService;
    private final RecomputeService recomputeService;

    @Override
    public SchemaUpdate createSchemaAndRecompute(Schema schema) {

        Schema saved = schemaService.create(schema);

        RecomputeResult recomputeResult = null;

        if (saved.isComputed()) {
            recomputeResult =
                    recomputeService.recomputeOnSchemaCreation(saved.getId());
        }

        return new SchemaUpdate(saved, null, recomputeResult);
    }

    @Override
    public SchemaUpdate updateSchemaAndRecompute(String schemaId, Schema schema) {

        SchemaUpdate saved = schemaService.update(schemaId, schema);


        RecomputeResult recomputeResult = null;

        if (saved.getSchema().isComputed() && Boolean.TRUE.equals(saved.getIsFormulaChanged())) {
            recomputeResult =
                    recomputeService.recomputeOnDefinitionChange(
                            PageType.DASHBOARD.key(),
                            schemaId
                    );
        }

        saved.setRecomputeResult(recomputeResult);

        return saved;
    }
}