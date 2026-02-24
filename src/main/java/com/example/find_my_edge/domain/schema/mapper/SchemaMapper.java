package com.example.find_my_edge.domain.schema.mapper;

import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.domain.schema.entity.SchemaEntity;
import com.example.find_my_edge.domain.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.hibernate.tool.schema.extract.spi.SchemaExtractionException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SchemaMapper {

    private final JsonUtil jsonUtil;

    /* ---------------- schema → ENTITY ---------------- */
    public SchemaEntity toEntity(Schema schema) {

        if (schema == null) return null;

        try {
            return SchemaEntity.builder()
                               .label(schema.getLabel())
                               .type(schema.getType())
                               .semanticType(schema.getSemanticType())
                               .mode(schema.getMode())

                               .astJson(jsonUtil.toJson(schema.getAst()))
                               .formula(schema.getFormula())
                               .dependencies(schema.getDependencies())

                               .initialValue(schema.getInitialValue())

                               .displayJson(jsonUtil.toJson(schema.getDisplay()))
                               .colorRulesJson(jsonUtil.toJson(schema.getColorRules()))
                               .optionsJson(jsonUtil.toJson(schema.getOptions()))

                               .build();

        } catch (Exception e) {
            throw new SchemaExtractionException("Failed to map schema → Entity", e);
        }
    }

    /* ---------------- ENTITY → schema ---------------- */
    public Schema toModel(SchemaEntity entity) {

        if (entity == null) return null;

        try {
            return Schema.builder()
                         .id(entity.getId())
                         .label(entity.getLabel())
                         .type(entity.getType())
                         .semanticType(entity.getSemanticType())
                         .mode(entity.getMode())

                         .ast(jsonUtil.fromJson(entity.getAstJson(), AstConfig.class))
                         .formula(entity.getFormula())
                         .dependencies(entity.getDependencies())

                         .source(entity.getSource())
                         .editable(entity.getEditable())
                         .initialValue(entity.getInitialValue())

                         .display(jsonUtil.fromJson(entity.getDisplayJson(), DisplayConfig.class))
                         .colorRules(jsonUtil.fromJsonList(
                                 entity.getColorRulesJson(), ColorRuleConfig.class
                         ))
                         .options(jsonUtil.fromJsonList(
                                 entity.getOptionsJson(), String.class
                         ))
                         .build();

        } catch (Exception e) {
            throw new SchemaExtractionException("Failed to map Entity → schema", e);
        }
    }

}