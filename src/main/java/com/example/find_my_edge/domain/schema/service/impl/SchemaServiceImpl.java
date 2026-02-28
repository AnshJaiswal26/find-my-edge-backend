package com.example.find_my_edge.domain.schema.service.impl;

import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.domain.schema.enums.SchemaRole;
import com.example.find_my_edge.domain.schema.enums.SchemaSource;
import com.example.find_my_edge.domain.schema.enums.ViewType;
import com.example.find_my_edge.domain.schema.exception.SchemaDependencyException;
import com.example.find_my_edge.domain.schema.exception.SchemaNotFoundException;
import com.example.find_my_edge.domain.schema.exception.SchemaOperationNotAllowedException;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.domain.schema.exception.SchemaOrderException;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import com.example.find_my_edge.domain.schema.registry.SchemaRegistry;
import com.example.find_my_edge.domain.schema.entity.SchemaEntity;
import com.example.find_my_edge.domain.schema.entity.SchemaOrderEntity;
import com.example.find_my_edge.domain.schema.mapper.SchemaMapper;
import com.example.find_my_edge.domain.schema.repository.SchemaOrderRepository;
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
    private final SchemaOrderRepository schemaOrderRepository;

    private final SchemaRegistry schemaRegistry;
    private final SchemaOverrideService overrideService;

    private final SchemaMapper mapper;
    private final JsonUtil jsonUtil;

    /* ---------------- CREATE ---------------- */
    @Override
    public Schema create(Schema schema) {

        String userId = authService.getCurrentUserId();

        SchemaEntity entity = mapper.toEntity(schema);

        // SET SOURCE + ROLE (CRITICAL)
        if (schema.isComputed()) {
            entity.setSource(SchemaSource.COMPUTED);
            entity.setRole(SchemaRole.USER_DEFINED);
        } else {
            entity.setSource(SchemaSource.USER);
            entity.setRole(SchemaRole.USER_DEFINED);
        }

        // HANDLE COMPUTED
        if (schema.isComputed() && schema.hasFormula()) {

            AstResult astResult = astPipeline.buildAst(schema.getFormula());

            entity.setAstJson(jsonUtil.toJson(astResult.getAstNode()));
            entity.setDependencies(new ArrayList<>(astResult.getDependencies()));
        }

        entity.setUserId(userId);

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
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        // SYSTEM PROTECTION
        if (existing.getRole() != SchemaRole.USER_DEFINED) {
            throw new SchemaOperationNotAllowedException("System schema cannot be modified");
        }

        SchemaEntity incoming = mapper.toEntity(schema);

        applyAllowedUpdates(existing, incoming);

        // HANDLE COMPUTED UPDATE (CRITICAL)
        if (existing.getSource() == SchemaSource.COMPUTED &&
            existing.getFormula() != null &&
            !existing.getFormula().isBlank()) {

            AstResult astResult = astPipeline.buildAst(existing.getFormula());

            existing.setAstJson(jsonUtil.toJson(astResult.getAstNode()));
            existing.setDependencies(new ArrayList<>(astResult.getDependencies()));
        }

        return mapper.toModel(schemaRepository.save(existing));
    }

    private void applyAllowedUpdates(SchemaEntity existing, SchemaEntity incoming) {

        switch (existing.getRole()) {

            case SYSTEM_REQUIRED, SYSTEM_OPTIONAL -> {
                existing.setHidden(incoming.getHidden());
                existing.setDisplayJson(incoming.getDisplayJson());
                existing.setColorRulesJson(incoming.getColorRulesJson());
            }

            case USER_DEFINED -> {
                existing.setLabel(incoming.getLabel());
                existing.setFormula(incoming.getFormula());
                existing.setDependencies(incoming.getDependencies());
                existing.setDisplayJson(incoming.getDisplayJson());
                existing.setColorRulesJson(incoming.getColorRulesJson());
                existing.setHidden(incoming.getHidden());
            }
        }
    }

    /* ---------------- GET BY ID ---------------- */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Schema getById(String id) {

        String userId = authService.getCurrentUserId();

        Schema system = schemaRegistry.get(id);
        if (system != null) {
            return overrideService.applyOverride(system, userId);
        }

        SchemaEntity entity = schemaRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new SchemaNotFoundException(id));

        return mapper.toModel(entity);
    }

    /* ---------------- GET ALL ---------------- */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public SchemaBundle getAll() {

        String userId = authService.getCurrentUserId();

        List<Schema> result = new ArrayList<>();
        Map<String, Schema> byId = new HashMap<>();

        List<Schema> registeredSchemas = schemaRegistry.getAll();

        List<Schema> mergedSystems =
                overrideService.applyOverrides(registeredSchemas, userId);

        for (Schema schema : mergedSystems) {
            result.add(schema);
            byId.put(schema.getId(), schema);
        }

        // user
        List<SchemaEntity> userSchemas = schemaRepository.findAllByUserId(userId);

        for (SchemaEntity entity : userSchemas) {
            Schema schema = mapper.toModel(entity);
            result.add(schema);
            byId.put(schema.getId(), schema);
        }

        List<String> order = getUserOrder(userId);

        if (order.isEmpty()) {
            order.addAll(schemaRegistry.getOrder());
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
                .orElseThrow(() -> new SchemaNotFoundException(id));

        if (schema.getRole() != SchemaRole.USER_DEFINED) {
            throw new SchemaOperationNotAllowedException(
                    "This schema cannot be deleted. You can hide it instead."
            );
        }

        List<String> dependents =
                schemaRepository.findAllByUserId(userId)
                                .stream()
                                .filter(s -> s.getDependencies() != null &&
                                             s.getDependencies().contains(id))
                                .map(SchemaEntity::getLabel)
                                .toList();

        if (!dependents.isEmpty()) {
            throw new SchemaDependencyException(
                    "Cannot delete '" + schema.getLabel() +
                    "' used by: " + String.join(", ", dependents)
            );
        }

        schemaRepository.delete(schema);

        updateOrderOnDelete(userId, id);
    }

    /* ---------------- ORDER ---------------- */

    @Override
    public List<String> updateOrder(List<String> order, ViewType viewType) {

        String userId = authService.getCurrentUserId();

        if (order == null || order.isEmpty()) {
            throw new SchemaOrderException("Order cannot be empty");
        }

        // 1. Collect ALL valid ids (user + system)
        Set<String> validIds = new HashSet<>();

        // user schemas
        validIds.addAll(
                schemaRepository.findAllByUserId(userId)
                                .stream()
                                .map(SchemaEntity::getId)
                                .toList()
        );

        // system schemas
        validIds.addAll(schemaRegistry.getOrder());

        //  2. Validate input
        for (String id : order) {
            if (!validIds.contains(id)) {
                throw new SchemaOrderException(
                        "Invalid schema id in order: " + id
                );
            }
        }

        // 3. Remove duplicates (preserve order)
        List<String> cleanedOrder = new ArrayList<>(new LinkedHashSet<>(order));

        //  4. (Optional but recommended) Ensure completeness
        for (String id : validIds) {
            if (!cleanedOrder.contains(id)) {
                cleanedOrder.add(id);
            }
        }

        // 5. Fetch or create entity
        SchemaOrderEntity entity = schemaOrderRepository
                .findByUserIdAndViewType(userId, viewType)
                .orElseGet(() -> {
                    SchemaOrderEntity e = new SchemaOrderEntity();
                    e.setUserId(userId);
                    e.setViewType(viewType);
                    return e;
                });

        // 6. Save using correct util
        entity.setOrder(jsonUtil.toJsonList(cleanedOrder));

        schemaOrderRepository.save(entity);

        return cleanedOrder;
    }

    /* ---------------- HELPERS ---------------- */

    private void updateOrderOnCreate(String userId, String schemaId) {

        SchemaOrderEntity entity = schemaOrderRepository
                .findByUserIdAndViewType(userId, ViewType.DEFAULT)
                .orElseGet(() -> {
                    SchemaOrderEntity e = new SchemaOrderEntity();
                    e.setUserId(userId);
                    e.setViewType(ViewType.DEFAULT);
                    e.setOrder(jsonUtil.toJson(new ArrayList<>()));
                    return e;
                });

        List<String> order = jsonUtil.fromJsonList(entity.getOrder(), String.class);

        order.add(schemaId);

        entity.setOrder(jsonUtil.toJson(order));
        schemaOrderRepository.save(entity);
    }

    private void updateOrderOnDelete(String userId, String schemaId) {

        schemaOrderRepository
                .findByUserIdAndViewType(userId, ViewType.DEFAULT)
                .ifPresent(entity -> {
                    List<String> order = jsonUtil.fromJsonList(entity.getOrder(), String.class);
                    order.remove(schemaId);

                    entity.setOrder(jsonUtil.toJson(order));
                    schemaOrderRepository.save(entity);
                });
    }

    private List<String> getUserOrder(String userId) {
        return schemaOrderRepository.
                findByUserId(userId)
                .map(e -> jsonUtil.fromJsonList(e.getOrder(), String.class))
                .orElse(new ArrayList<>());
    }

}