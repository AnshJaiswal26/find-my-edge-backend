package com.example.find_my_edge.schema.service.impl;

import com.example.find_my_edge.common.config.uiconfigs.ColorRuleConfig;
import com.example.find_my_edge.common.config.uiconfigs.DisplayConfig;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.schema.entity.SchemaOverrideEntity;
import com.example.find_my_edge.schema.exception.SchemaOverrideException;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.repository.SchemaOverrideRepository;
import com.example.find_my_edge.schema.service.SchemaOverrideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchemaOverrideServiceImpl implements SchemaOverrideService {

    private final SchemaOverrideRepository overrideRepository;
    private final JsonUtil jsonUtil;

    @Override
    public List<Schema> applyOverrides(List<Schema> systems, UUID userId) {

        if (systems == null || systems.isEmpty()) return systems;

        // 1 query instead of N
        List<SchemaOverrideEntity> overrides =
                overrideRepository.findAllByUserId(userId);

        // map by schemaId
        Map<String, SchemaOverrideEntity> overrideMap =
                overrides.stream()
                         .collect(Collectors.toMap(
                                 SchemaOverrideEntity::getSchemaId,
                                 o -> o
                         ));

        List<Schema> result = new ArrayList<>();

        for (Schema system : systems) {

            SchemaOverrideEntity override = overrideMap.get(system.getId());

            if (override == null) {
                result.add(system);
                continue;
            }

            result.add(applySingleOverride(system, override));
        }

        return result;
    }

    @Override
    public Schema applySingleOverride(Schema system, SchemaOverrideEntity override) {

        Schema copy = copySchema(system);

        try {
            if (override.getHidden() != null) {
                copy.setHidden(override.getHidden());
            }

            if (override.getDisplayJson() != null) {
                DisplayConfig display =
                        jsonUtil.fromJson(override.getDisplayJson(), DisplayConfig.class);

                if (display != null) {
                    copy.setDisplay(display);
                }
            }

            if (override.getColorRulesJson() != null) {
                var rules = jsonUtil.fromJsonList(
                        override.getColorRulesJson(),
                        ColorRuleConfig.class
                );

                if (rules != null) {
                    copy.setColorRules(rules);
                }
            }

        } catch (Exception e) {
            throw new SchemaOverrideException("Override parsing failed");
        }

        return copy;
    }

    @Override
    public Schema applyOverride(Schema system, UUID userId) {

        if (system == null) return null;

        Optional<SchemaOverrideEntity> optional =
                overrideRepository.findByUserIdAndSchemaId(userId, system.getId());

        if (optional.isEmpty()) {
            return system;
        }

        SchemaOverrideEntity override = optional.get();

        //  SAFE COPY (no json copy)
        Schema copy = copySchema(system);

        try {
            if (override.getDisplayJson() != null) {
                DisplayConfig display =
                        jsonUtil.fromJson(override.getDisplayJson(), DisplayConfig.class);

                if (display != null) {
                    copy.setDisplay(display);
                }
            }

            if (override.getColorRulesJson() != null) {
                var rules = jsonUtil.fromJsonList(
                        override.getColorRulesJson(),
                        ColorRuleConfig.class
                );

                if (rules != null) {
                    copy.setColorRules(rules);
                }
            }

        } catch (Exception e) {
            throw new SchemaOverrideException("Override parsing failed");
        }

        return copy;
    }

    @Override
    public SchemaOverrideEntity getOrExisting(String schemaId, UUID userId) {
        return overrideRepository.findByUserIdAndSchemaId(userId, schemaId)
                                 .orElseGet(() -> {
                                     SchemaOverrideEntity schemaOverrideEntity = new SchemaOverrideEntity();
                                     schemaOverrideEntity.setUserId(userId);
                                     schemaOverrideEntity.setSchemaId(schemaId);
                                     return schemaOverrideEntity;
                                 });
    }

    @Override
    public SchemaOverrideEntity save(SchemaOverrideEntity schemaOverrideEntity) {
        return overrideRepository.save(schemaOverrideEntity);
    }

    private Schema copySchema(Schema system) {
        return Schema.builder()
                     .id(system.getId())
                     .label(system.getLabel())
                     .type(system.getType())
                     .semanticType(system.getSemanticType())
                     .mode(system.getMode())
                     .ast(system.getAst())
                     .formula(system.getFormula())
                     .idFormula(system.getIdFormula())
                     .dependencies(system.getDependencies())
                     .source(system.getSource())
                     .role(system.getRole())
                     .initialValue(system.getInitialValue())
                     .display(system.getDisplay())
                     .colorRules(system.getColorRules())
                     .options(system.getOptions())
                     .hidden(system.getHidden())
                     .build();
    }
}