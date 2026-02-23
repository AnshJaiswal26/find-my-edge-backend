package com.example.find_my_edge.core.schema.mapper;

import com.example.find_my_edge.common.dto.AstDTO;
import com.example.find_my_edge.common.dto.DisplayDTO;
import com.example.find_my_edge.core.schema.dto.SchemaResponseDTO;
import com.example.find_my_edge.core.schema.dto.SchemaRequestDTO;
import com.example.find_my_edge.core.schema.entity.SchemaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SchemaMapper {

    private final ObjectMapper objectMapper;

    /* ---------------- DTO → ENTITY ---------------- */
    public SchemaEntity toEntity(SchemaRequestDTO dto) {

        if (dto == null) return null;

        try {
            return SchemaEntity.builder()
                               .label(dto.getLabel())
                               .type(dto.getType())
                               .semanticType(dto.getSemanticType())
                               .mode(dto.getMode())

                               .astJson(write(dto.getAst()))
                               .formula(dto.getFormula())
                               .dependencies(dto.getDependencies())

                               .initialValue(dto.getInitialValue())

                               .displayJson(write(dto.getDisplay()))
                               .colorRulesJson(write(dto.getColorRules()))
                               .optionsJson(write(dto.getOptions()))

                               .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to map DTO → Entity", e);
        }
    }

    /* ---------------- ENTITY → DTO ---------------- */
    public SchemaResponseDTO toDTO(SchemaEntity entity) {

        if (entity == null) return null;

        try {
            return SchemaResponseDTO.builder()
                                    .id(entity.getId())
                                    .label(entity.getLabel())
                                    .type(entity.getType())
                                    .semanticType(entity.getSemanticType())
                                    .mode(entity.getMode())

                                    .ast(read(entity.getAstJson(), AstDTO.class))
                                    .formula(entity.getFormula())
                                    .dependencies(entity.getDependencies())

                                    .source(entity.getSource())
                                    .editable(entity.getEditable())
                                    .initialValue(entity.getInitialValue())

                                    .display(read(entity.getDisplayJson(), DisplayDTO.class))
                                    .colorRules(readList(
                                    entity.getColorRulesJson(), new TypeReference<>() {
                                    }
                            ))
                                    .options(readList(
                                    entity.getOptionsJson(), new TypeReference<>() {
                                    }
                            ))

                                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to map Entity → DTO", e);
        }
    }

    /* ---------------- COPY (IMPORTANT) ---------------- */
    public SchemaResponseDTO copy(SchemaResponseDTO dto) {
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(dto),
                    SchemaResponseDTO.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy SchemaDTO", e);
        }
    }

    /* ---------------- HELPERS ---------------- */

    private String write(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON write failed", e);
        }
    }

    private <T> T read(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON read failed", e);
        }
    }

    private <T> List<T> readList(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.isBlank()) return List.of();

        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("JSON list read failed", e);
        }
    }
}