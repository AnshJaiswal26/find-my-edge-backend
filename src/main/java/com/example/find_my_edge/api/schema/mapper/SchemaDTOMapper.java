package com.example.find_my_edge.api.schema.mapper;

import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.api.schema.dto.SchemaRequestDTO;
import com.example.find_my_edge.api.schema.dto.SchemaResponseDTO;
import com.example.find_my_edge.api.schema.dto.SchemaResponseDTOBundle;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import lombok.RequiredArgsConstructor;
import org.hibernate.tool.schema.extract.spi.SchemaExtractionException;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@RequiredArgsConstructor
public class SchemaDTOMapper {

    private final JsonUtil jsonUtil;

    public Schema toSchema(SchemaRequestDTO dto) {

        if (dto == null) return null;

        try {
            return Schema.builder()
                         .label(dto.getLabel())
                         .type(dto.getType())
                         .semanticType(dto.getSemanticType())
                         .mode(dto.getMode())

                         .ast(jsonUtil.copy(dto.getAst(), AstConfig.class))
                         .formula(dto.getFormula())
                         .dependencies(dto.getDependencies())

                         .initialValue(dto.getInitialValue())

                         .display(jsonUtil.copy(dto.getDisplay(), DisplayConfig.class))
                         .colorRules(Collections.singletonList(jsonUtil.copy(dto.getColorRules(), ColorRuleConfig.class)))
                         .options(new ArrayList<>(dto.getOptions()))
                         .build();

        } catch (Exception e) {
            throw new SchemaExtractionException("Failed to map schema → Entity", e);
        }
    }

    public SchemaResponseDTO toDTO(Schema schema) {

        if (schema == null) return null;

        try {
            return SchemaResponseDTO.builder()
                                    .id(schema.getId())
                                    .label(schema.getLabel())
                                    .type(schema.getType())
                                    .semanticType(schema.getSemanticType())
                                    .mode(schema.getMode())

                                    .ast(jsonUtil.copy(schema.getAst(), AstConfig.class))
                                    .formula(schema.getFormula())
                                    .dependencies(schema.getDependencies())

                                    .source(schema.getSource())
                                    .editable(schema.getEditable())
                                    .initialValue(schema.getInitialValue())

                                    .display(toDisplayDTO(schema.getDisplay()))
                                    .colorRules(toColorRulesDTO(schema.getColorRules()))
                                    .options(new ArrayList<>(schema.getOptions()))
                                    .build();

        } catch (Exception e) {
            throw new SchemaExtractionException("Failed to map Entity → schema", e);
        }
    }

    public SchemaResponseDTOBundle toSchemaDTOBundle(SchemaBundle schemaBundle) {

        Map<String, SchemaResponseDTO> schemasById = new HashMap<>();

        // 1. Convert all schemas
        for (Schema schema : schemaBundle.getSchemas()) {
            SchemaResponseDTO dto = toDTO(schema);
            schemasById.put(dto.getId(), dto);
        }

        // 2. Build ordered list using correct order
        List<SchemaResponseDTO> schemas = new ArrayList<>();

        for (String id : schemaBundle.getSchemasOrder()) {
            SchemaResponseDTO dto = schemasById.get(id);
            if (dto != null) {
                schemas.add(dto);
            }
        }

        return SchemaResponseDTOBundle.builder()
                                      .schemas(schemas)
                                      .schemasById(schemasById)
                                      .schemasOrder(schemaBundle.getSchemasOrder()) // real order
                                      .build();
    }

    public DisplayConfig toDisplayDTO(DisplayConfig display) {
        return DisplayConfig.builder()
                            .format(display.getFormat())
                            .decimals(display.getDecimals())
                            .build();
    }

    public List<ColorRuleConfig> toColorRulesDTO(List<ColorRuleConfig> colorRules) {

        return colorRules
                .stream()
                .map(cr -> ColorRuleConfig
                        .builder()
                        .color(cr.getColor())
                        .operator(cr.getOperator())
                        .value(cr.getValue())
                        .value2(cr.getValue2())
                        .build()
                )
                .toList();
    }
}