package com.example.find_my_edge.api.schema.mapper;

import com.example.find_my_edge.api.schema.dto.SchemaRequestDto;
import com.example.find_my_edge.api.schema.dto.SchemaResponseDto;
import com.example.find_my_edge.api.schema.dto.SchemaResponseDTOBundle;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@RequiredArgsConstructor
public class SchemaDtoMapper {

    private final JsonUtil jsonUtil;

    /* ---------------- REQUEST → DOMAIN ---------------- */
    public Schema toSchema(SchemaRequestDto dto) {

        if (dto == null) return null;

        return Schema.builder()
                     .label(dto.getLabel())
                     .type(dto.getType())
                     .semanticType(dto.getSemanticType())
                     .mode(dto.getMode())
                     .hidden(dto.getHidden())

                     .source(dto.getSource())
                     .role(dto.getRole())

                     .ast(copy(dto.getAst(), AstConfig.class))
                     .formula(dto.getFormula())
                     .dependencies(safeList(dto.getDependencies()))

                     .initialValue(dto.getInitialValue())

                     .display(copy(dto.getDisplay(), DisplayConfig.class))
                     .colorRules(jsonUtil.fromList(dto.getColorRules(), ColorRuleConfig.class))
                     .options(safeList(dto.getOptions()))

                     .build();
    }

    /* ---------------- DOMAIN → RESPONSE ---------------- */

    public SchemaResponseDto toResponse(Schema schema) {

        if (schema == null) return null;

        SchemaResponseDto dto = new SchemaResponseDto();

        dto.setId(schema.getId());
        dto.setLabel(schema.getLabel());
        dto.setHidden(schema.getHidden());

        dto.setType(schema.getType());
        dto.setSemanticType(schema.getSemanticType());
        dto.setMode(schema.getMode());

        dto.setAst(copy(schema.getAst(), AstConfig.class));
        dto.setFormula(schema.getFormula());
        dto.setDependencies(safeList(schema.getDependencies()));

        dto.setInitialValue(schema.getInitialValue());

        dto.setDisplay(copy(schema.getDisplay(), DisplayConfig.class));
        dto.setColorRules(copyList(schema.getColorRules(), ColorRuleConfig.class));
        dto.setOptions(safeList(schema.getOptions()));

        dto.setSource(schema.getSource());
        dto.setRole(schema.getRole());

        return dto;
    }

    /* ---------------- BUNDLE ---------------- */

    public SchemaResponseDTOBundle toSchemaDTOBundle(SchemaBundle bundle) {

        Map<String, SchemaResponseDto> byId = new HashMap<>();

        for (Schema schema : bundle.getSchemas()) {
            SchemaResponseDto dto = toResponse(schema);
            byId.put(dto.getId(), dto);
        }

        List<SchemaResponseDto> ordered = new ArrayList<>();

        for (String id : bundle.getSchemasOrder()) {
            SchemaResponseDto dto = byId.get(id);
            if (dto != null) {
                ordered.add(dto);
            }
        }

        SchemaResponseDTOBundle result = new SchemaResponseDTOBundle();
        result.setSchemas(ordered);
        result.setSchemasById(byId);
        result.setSchemasOrder(bundle.getSchemasOrder());

        return result;
    }

    /* ---------------- HELPERS ---------------- */

    private <T> T copy(T source, Class<T> clazz) {
        return source == null ? null : jsonUtil.copy(source, clazz);
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    private <T> List<T> copyList(List<T> list, Class<T> clazz) {
        if (list == null) return new ArrayList<>();

        return list.stream()
                   .map(item -> jsonUtil.copy(item, clazz))
                   .toList();
    }
}