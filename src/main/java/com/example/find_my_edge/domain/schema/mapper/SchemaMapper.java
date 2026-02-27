package com.example.find_my_edge.domain.schema.mapper;

import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.domain.schema.entity.SchemaEntity;
import com.example.find_my_edge.domain.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchemaMapper {

    private final JsonUtil jsonUtil;

    /* ---------------- MODEL → ENTITY ---------------- */
    public SchemaEntity toEntity(Schema schema) {

        if (schema == null) return null;

        try {
            return SchemaEntity.builder()
                               .label(schema.getLabel())
                               .type(schema.getType())
                               .hidden(schema.getHidden())

                               .semanticType(schema.getSemanticType())
                               .mode(schema.getMode())

                               .astJson(jsonUtil.toJson(schema.getAst()))
                               .formula(schema.getFormula())
                               .dependencies(safeList(schema.getDependencies()))

                               .initialValue(schema.getInitialValue())

                               .displayJson(jsonUtil.toJson(schema.getDisplay()))
                               .colorRulesJson(jsonUtil.toJson(safeList(schema.getColorRules())))
                               .optionsJson(jsonUtil.toJson(safeList(schema.getOptions())))

                               .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to map Schema → Entity", e);
        }
    }

    /* ---------------- ENTITY → MODEL ---------------- */
    public Schema toModel(SchemaEntity entity) {

        if (entity == null) return null;

        try {
            return Schema.builder()
                         .id(entity.getId())
                         .label(entity.getLabel())
                         .hidden(entity.getHidden())

                         .type(entity.getType())
                         .semanticType(entity.getSemanticType())
                         .mode(entity.getMode())

                         .ast(jsonUtil.fromJson(entity.getAstJson(), AstConfig.class))
                         .formula(entity.getFormula())
                         .dependencies(safeList(entity.getDependencies()))

                         // safe to expose
                         .source(entity.getSource())
                         .role(entity.getRole())

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
            throw new RuntimeException("Failed to map Entity → Schema", e);
        }
    }

    /* ---------------- HELPERS ---------------- */

    private <T> java.util.List<T> safeList(java.util.List<T> list) {
        return list == null ? new java.util.ArrayList<>() : list;
    }
}

