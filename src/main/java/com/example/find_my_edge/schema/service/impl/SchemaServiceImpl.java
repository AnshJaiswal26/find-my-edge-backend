package com.example.find_my_edge.schema.service.impl;

import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.schema.entity.SchemaOverrideEntity;
import com.example.find_my_edge.schema.enums.SchemaRole;
import com.example.find_my_edge.schema.enums.SchemaSource;
import com.example.find_my_edge.schema.enums.ViewType;
import com.example.find_my_edge.schema.exception.SchemaDependencyException;
import com.example.find_my_edge.schema.exception.SchemaNotFoundException;
import com.example.find_my_edge.schema.exception.SchemaOperationNotAllowedException;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.schema.exception.SchemaOrderException;
import com.example.find_my_edge.schema.mapper.SchemaDtoMapper;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaBundle;
import com.example.find_my_edge.schema.registry.SchemaRegistry;
import com.example.find_my_edge.schema.entity.SchemaEntity;
import com.example.find_my_edge.schema.entity.SchemaOrderEntity;
import com.example.find_my_edge.schema.mapper.SchemaMapper;
import com.example.find_my_edge.schema.repository.SchemaOrderRepository;
import com.example.find_my_edge.schema.repository.SchemaRepository;
import com.example.find_my_edge.schema.service.SchemaOverrideService;
import com.example.find_my_edge.schema.service.SchemaService;

import com.example.find_my_edge.workspace.service.WorkspaceService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SchemaServiceImpl implements SchemaService {

    private final CurrentUserService currentUserService;
    private final AstPipeline astPipeline;

    private final SchemaRepository schemaRepository;
    private final SchemaOrderRepository schemaOrderRepository;

    private final SchemaRegistry schemaRegistry;
    private final SchemaOverrideService overrideService;

    private final WorkspaceService workspaceService;

    private final SchemaDtoMapper dtoMapper;
    private final SchemaMapper mapper;
    private final JsonUtil jsonUtil;

    /* ---------------- CREATE ---------------- */
    @Override
    @Transactional
    public Schema create(Schema schema) {

        schema.validateForWrite();

        UUID userId = currentUserService.getUserId();

        SchemaEntity entity = mapper.toEntity(schema);

        if (schemaRepository.existsByUserIdAndLabel(userId, entity.getLabel())) {
            throw new SchemaDependencyException(
                    "Schema with label '" + entity.getLabel() + "' already exists"
            );
        }

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

            AstResult astResult = astPipeline.buildAst(schema.getIdFormula());

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
    @Transactional
    public Schema update(String schemaId, Schema schema) {
        System.out.println(schema);
        schema.validateForWrite();

        UUID userId = currentUserService.getUserId();

        SchemaEntity existing = schemaRepository
                .findByIdAndUserId(schemaId, userId)
                .orElse(null);

        if (schemaRegistry.exists(schemaId)) {

            SchemaOverrideEntity schemaOverrideEntity =
                    overrideService.getOrExisting(schemaId, userId);

            schemaOverrideEntity.setHidden(schema.getHidden());
            schemaOverrideEntity.setColorRulesJson(jsonUtil.toJsonList(schema.getColorRules()));
            schemaOverrideEntity.setDisplayJson(jsonUtil.toJson(schema.getDisplay()));

            overrideService.save(schemaOverrideEntity);

            Schema baseSchema = schemaRegistry.get(schemaId);

            return overrideService.applySingleOverride(baseSchema, schemaOverrideEntity);

        } else if (existing == null) {
            throw new SchemaNotFoundException(schemaId);
        }

        SchemaEntity incoming = mapper.toEntity(schema);

        incoming.setId(existing.getId());

        applyUpdateStrategy(existing, incoming);

        SchemaEntity saved = schemaRepository.save(existing);

        return mapper.toModel(saved);
    }

    private void applyUpdateStrategy(SchemaEntity existing, SchemaEntity incoming) {

        SchemaRole role = existing.getRole();
        SchemaSource source = existing.getSource();

        // USER_DEFINED → full control
        if (role == SchemaRole.USER_DEFINED) {

            if (source == SchemaSource.COMPUTED) {
                applyComputedUserUpdate(existing, incoming);
            } else {
                applyUserUpdate(existing, incoming);
            }
        }
    }

    private void applyUserUpdate(SchemaEntity existing, SchemaEntity incoming) {

        existing.setLabel(incoming.getLabel());
        existing.setFormula(incoming.getFormula());
        existing.setIdFormula(incoming.getIdFormula());
        existing.setDependencies(incoming.getDependencies());

        existing.setDisplayJson(incoming.getDisplayJson());
        existing.setColorRulesJson(incoming.getColorRulesJson());
        existing.setHidden(incoming.getHidden());
    }

    private void applyComputedUserUpdate(SchemaEntity existing, SchemaEntity incoming) {

        existing.setLabel(incoming.getLabel());
        existing.setFormula(incoming.getFormula());
        existing.setIdFormula(incoming.getIdFormula());
        existing.setDependencies(incoming.getDependencies());

        existing.setDisplayJson(incoming.getDisplayJson());
        existing.setColorRulesJson(incoming.getColorRulesJson());
        existing.setHidden(incoming.getHidden());

        //  CRITICAL: AST validation here (not outside)
        if (existing.getIdFormula() != null && !existing.getIdFormula().isBlank()) {

            AstResult astResult = astPipeline.buildAst(existing.getIdFormula());

            existing.setAstJson(jsonUtil.toJson(astResult.getAstNode()));
            existing.setDependencies(new ArrayList<>(astResult.getDependencies()));
        }
    }

    /* ---------------- GET BY ID ---------------- */
    @Override
    public Schema getById(String id) {

        UUID userId = currentUserService.getUserId();

        Schema system = schemaRegistry.get(id);
        if (system != null) {
            Schema override = overrideService.applyOverride(system, userId);
            dtoMapper.toResponse(override);
        }

        SchemaEntity entity = schemaRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new SchemaNotFoundException(id));

        return mapper.toModel(entity);
    }

    /* ---------------- GET ALL ---------------- */
    @Override
    public SchemaBundle getAll() {

        UUID userId = currentUserService.getUserId();

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

        List<String> order = getUserOrder(userId, ViewType.DEFAULT);

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
    @Transactional
    public void delete(String id) {

        UUID userId = currentUserService.getUserId();

        SchemaEntity schema = schemaRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new SchemaNotFoundException(id));

        if (schema.getRole() != SchemaRole.USER_DEFINED) {
            throw new SchemaOperationNotAllowedException(
                    "This schema cannot be deleted. You can hide it instead."
            );
        }

        List<String> dependents =
                schemaRepository.findDependentSchemaLabels(userId, id);

        if (!dependents.isEmpty()) {
            throw new SchemaDependencyException(
                    "Cannot delete '" + schema.getLabel() +
                    "' used by: " + String.join(", ", dependents)
            );
        }

        schemaRepository.delete(schema);

        workspaceService.removeSchemaReferences(id);

        updateOrderOnDelete(userId, id);
    }

    /* ---------------- ORDER ---------------- */

    @Override
    public List<String> getOrder(ViewType viewType) {

        UUID userId = currentUserService.getUserId();

        List<String> defaultOrder = getUserOrder(userId, ViewType.DEFAULT);

        if (viewType == ViewType.DEFAULT) {
            return defaultOrder;
        }

        List<String> viewOrder = getUserOrder(userId, viewType);

        if (defaultOrder.isEmpty() && viewOrder.isEmpty()) {
            return new ArrayList<>(schemaRegistry.getOrder());
        }

        Set<String> defaultSet = new LinkedHashSet<>(defaultOrder);

        // Merge: preserve custom order, append missing schemas
        List<String> merged = new ArrayList<>();

        // Keep only valid IDs from viewOrder
        for (String id : viewOrder) {
            if (defaultSet.contains(id)) {
                merged.add(id);
            }
        }

        // Append missing ones from default
        for (String id : defaultOrder) {
            if (!merged.contains(id)) {
                merged.add(id);
            }
        }

        return merged;
    }

    @Override
    @Transactional
    public List<String> updateOrder(List<String> order, ViewType viewType) {

        System.out.println("updateOrder() method called");

        UUID userId = currentUserService.getUserId();

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

        System.out.println("Before save: " + entity);
        schemaOrderRepository.save(entity);
        System.out.println("After save");

        return cleanedOrder;
    }

    /* ---------------- HELPERS ---------------- */

    private void updateOrderOnCreate(UUID userId, String schemaId) {

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

        if (order.isEmpty()) {
            order.addAll(schemaRegistry.getOrder());
        }

        order.add(schemaId);

        entity.setOrder(jsonUtil.toJson(order));
        schemaOrderRepository.save(entity);
    }

    private void updateOrderOnDelete(UUID userId, String schemaId) {

        List<SchemaOrderEntity> allByUserId =
                schemaOrderRepository.findAllByUserId(userId);

        if (allByUserId.isEmpty()) return;

        List<SchemaOrderEntity> entityList =
                allByUserId.stream().map(entity -> {
                    List<String> order = jsonUtil.fromJsonList(entity.getOrder(), String.class);
                    order.remove(schemaId);
                    entity.setOrder(jsonUtil.toJson(order));
                    return entity;
                }).toList();

        schemaOrderRepository.saveAll(entityList);
    }

    private List<String> getUserOrder(UUID userId, ViewType viewType) {
        return schemaOrderRepository.
                findByUserIdAndViewType(userId, viewType)
                .map(e -> jsonUtil.fromJsonList(e.getOrder(), String.class))
                .orElse(new ArrayList<>());
    }
}