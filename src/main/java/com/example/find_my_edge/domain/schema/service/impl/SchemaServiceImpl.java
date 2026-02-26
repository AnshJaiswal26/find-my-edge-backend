package com.example.find_my_edge.domain.schema.service.impl;

import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.domain.schema.exception.SchemaDependencyException;
import com.example.find_my_edge.domain.schema.exception.SchemaNotFoundException;
import com.example.find_my_edge.domain.schema.exception.SchemaOperationNotAllowedException;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.domain.schema.exception.SchemaOverrideException;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import com.example.find_my_edge.domain.schema.registry.SystemSchemaRegistry;
import com.example.find_my_edge.domain.schema.entity.SchemaEntity;
import com.example.find_my_edge.domain.schema.entity.SchemaOrderEntity;
import com.example.find_my_edge.domain.schema.enums.SchemaSource;
import com.example.find_my_edge.domain.schema.mapper.SchemaMapper;
import com.example.find_my_edge.domain.schema.repository.SchemaOrderRepository;
import com.example.find_my_edge.domain.schema.repository.SchemaOverrideRepository;
import com.example.find_my_edge.domain.schema.repository.SchemaRepository;
import com.example.find_my_edge.domain.schema.service.SchemaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SchemaServiceImpl implements SchemaService {

    private final AuthService authService;

    private final AstPipeline astPipeline;

    private final SchemaRepository schemaRepository;
    private final SchemaOverrideRepository overrideRepository;
    private final SchemaOrderRepository schemaOrderRepository;

    private final SystemSchemaRegistry systemRegistry;

    private final SchemaMapper mapper;

    private final JsonUtil jsonUtil;

    /* ---------------- CREATE ---------------- */
    @Override
    public Schema create(Schema schema) {

        String userId = authService.getCurrentUserId();

        SchemaEntity entity = mapper.toEntity(schema);

        if (schema.getSource() == SchemaSource.COMPUTED &&
            schema.getFormula() != null && !schema.getFormula().isBlank()) {

            AstResult astResult = astPipeline.buildAst(schema.getFormula(), "");

            entity.setAstJson(jsonUtil.toJson(astResult.getAstNode()));

            entity.setDependencies(new ArrayList<>(astResult.getDependencies()));
        }

        entity.setUserId(userId);
        entity.setSource(schema.getSource());

        SchemaEntity saved = schemaRepository.save(entity);

        updateOrderOnCreate(userId, saved.getId());

        return mapper.toModel(saved);
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public Schema update(String schemaId, Schema schema) {

        String userId = authService.getCurrentUserId();

        SchemaEntity existing = schemaRepository
                .findByIdAndUserId(schemaId, userId)
                .orElseThrow(() -> new SchemaNotFoundException("Schema not found"));

        if (existing.getSource() == SchemaSource.SYSTEM) {
            throw new SchemaOperationNotAllowedException("System schema cannot be updated");
        }

        SchemaEntity updated = mapper.toEntity(schema);

        updated.setId(existing.getId());
        updated.setUserId(userId);
        updated.setSource(SchemaSource.USER);

        return mapper.toModel(schemaRepository.save(updated));
    }

    /* ---------------- GET BY ID ---------------- */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Schema getById(String id) {

        String userId = authService.getCurrentUserId();

        // system
        Schema system = systemRegistry.get(id);
        if (system != null) {
            return applyOverride(system, userId);
        }

        // user
        SchemaEntity entity = schemaRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new SchemaNotFoundException("Schema not found"));

        return mapper.toModel(entity);
    }

    /* ---------------- GET ALL ---------------- */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public SchemaBundle getAll() {

        String userId = authService.getCurrentUserId();

        List<Schema> result = new ArrayList<>();
        Map<String, Schema> byId = new HashMap<>();

        // 1. system
        for (Schema system : systemRegistry.getAll()) {
            Schema merged = applyOverride(system, userId);
            result.add(merged);
            byId.put(merged.getId(), merged);
        }

        // 2. user
        List<SchemaEntity> userSchemas = schemaRepository.findAllByUserId(userId);

        for (SchemaEntity entity : userSchemas) {
            Schema schema = mapper.toModel(entity);
            result.add(schema);
            byId.put(schema.getId(), schema);
        }

        // 3. ORDER (IMPORTANT FIX)
        List<String> order = getUserOrder(userId);

        // fallback if empty
        if (order.isEmpty()) {
            order.addAll(systemRegistry.getOrder());
            order.addAll(userSchemas.stream().map(SchemaEntity::getId).toList());
        }

        return SchemaBundle.builder()
                           .schemas(result)
                           .schemasById(byId)
                           .schemasOrder(order)
                           .build();
    }

    /* ---------------- DELETE ---------------- */
    @Override
    public void delete(String id) {

        String userId = authService.getCurrentUserId();

        SchemaEntity schema = schemaRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new SchemaNotFoundException("Schema not found"));

        if (schema.getSource() == SchemaSource.SYSTEM) {
            throw new SchemaOperationNotAllowedException("System schema cannot be deleted");
        }

        // dependency check
        List<String> dependents = schemaRepository.findAllByUserId(userId)
                                                  .stream()
                                                  .filter(s -> s.getDependencies() != null && s.getDependencies().contains(id))
                                                  .map(SchemaEntity::getLabel)
                                                  .toList();

        if (!dependents.isEmpty()) {
            throw new SchemaDependencyException(
                    "Cannot delete '" + schema.getLabel() + "' used by: " + String.join(", ", dependents)
            );
        }

        schemaRepository.delete(schema);

        updateOrderOnDelete(userId, id);
    }

    @Override
    public List<String> updateOrder(List<String> order) {

        String userId = authService.getCurrentUserId();

        if (order == null || order.isEmpty()) {
            throw new IllegalArgumentException("Order cannot be empty");
        }

        // 1. Fetch user schemas
        List<SchemaEntity> userSchemas = schemaRepository.findAllByUserId(userId);

        Set<String> validIds = userSchemas.stream()
                                          .map(SchemaEntity::getId)
                                          .collect(Collectors.toSet());

        // 2. Validate order (only user's schemas allowed)
        for (String id : order) {
            if (!validIds.contains(id) && !systemRegistry.exists(id)) {
                throw new SchemaNotFoundException("Invalid schema id in order: " + id);
            }
        }

        // 3. Remove duplicates (important)
        List<String> cleanedOrder = new ArrayList<>(new LinkedHashSet<>(order));

        // 4. Save order
        SchemaOrderEntity entity = schemaOrderRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    SchemaOrderEntity e = new SchemaOrderEntity();
                    e.setUserId(userId);
                    return e;
                });

        entity.setOrder(jsonUtil.toJson(cleanedOrder));

        schemaOrderRepository.save(entity);

        return cleanedOrder;
    }

    /* ---------------- ORDER HELPERS ---------------- */
    private void updateOrderOnCreate(String userId, String schemaId) {

        SchemaOrderEntity entity = schemaOrderRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    SchemaOrderEntity e = new SchemaOrderEntity();
                    e.setUserId(userId);
                    e.setOrder(jsonUtil.toJson(new ArrayList<>()));
                    return e;
                });

        List<String> order = parseOrder(entity);

        order.add(schemaId);

        entity.setOrder(jsonUtil.toJson(order));
        schemaOrderRepository.save(entity);
    }

    private void updateOrderOnDelete(String userId, String schemaId) {

        schemaOrderRepository.findByUserId(userId).ifPresent(entity -> {
            List<String> order = parseOrder(entity);
            order.remove(schemaId);

            entity.setOrder(jsonUtil.toJson(order));
            schemaOrderRepository.save(entity);
        });
    }

    private List<String> getUserOrder(String userId) {
        return schemaOrderRepository.findByUserId(userId)
                                    .map(this::parseOrder)
                                    .orElse(new ArrayList<>());
    }

    private List<String> parseOrder(SchemaOrderEntity entity) {
        return entity.getOrder() == null
               ? new ArrayList<>()
               : jsonUtil.fromJsonList(entity.getOrder(), String.class);
    }

    /* ---------------- OVERRIDE ---------------- */
    private Schema applyOverride(Schema system, String userId) {

        return overrideRepository
                .findByUserIdAndSchemaId(userId, system.getId())
                .map(override -> {

                    Schema copy = jsonUtil.copy(system, Schema.class);

                    try {
                        if (override.getDisplayJson() != null) {
                            copy.setDisplay(
                                    jsonUtil.fromJson(override.getDisplayJson(), DisplayConfig.class)
                            );
                        }

                        if (override.getColorRulesJson() != null) {
                            copy.setColorRules(
                                    jsonUtil.fromJsonList(override.getColorRulesJson(), ColorRuleConfig.class)
                            );
                        }

                    } catch (Exception e) {
                        throw new SchemaOverrideException("Override parsing failed");
                    }

                    return copy;
                })
                .orElse(system); //  fallback if no override
    }
}