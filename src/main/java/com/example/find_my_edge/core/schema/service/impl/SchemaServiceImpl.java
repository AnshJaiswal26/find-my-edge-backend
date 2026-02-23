package com.example.find_my_edge.core.schema.service.impl;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.common.dto.ColorRuleDTO;
import com.example.find_my_edge.common.dto.DisplayDTO;
import com.example.find_my_edge.core.schema.exception.SchemaDependencyException;
import com.example.find_my_edge.core.schema.exception.SchemaNotFoundException;
import com.example.find_my_edge.core.schema.exception.SchemaOperationNotAllowedException;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.core.schema.exception.SchemaOverrideException;
import com.example.find_my_edge.core.schema.registry.SystemSchemaRegistry;
import com.example.find_my_edge.core.schema.dto.SchemaResponseDTO;
import com.example.find_my_edge.core.schema.dto.SchemaRequestDTO;
import com.example.find_my_edge.core.schema.entity.SchemaEntity;
import com.example.find_my_edge.core.schema.entity.SchemaOrderEntity;
import com.example.find_my_edge.core.schema.enums.SchemaSource;
import com.example.find_my_edge.core.schema.mapper.SchemaMapper;
import com.example.find_my_edge.core.schema.repository.SchemaOrderRepository;
import com.example.find_my_edge.core.schema.repository.SchemaOverrideRepository;
import com.example.find_my_edge.core.schema.repository.SchemaRepository;
import com.example.find_my_edge.core.schema.service.SchemaService;

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

    private final SchemaRepository schemaRepository;
    private final SchemaOverrideRepository overrideRepository;
    private final SchemaOrderRepository schemaOrderRepository;

    private final SystemSchemaRegistry systemRegistry;

    private final SchemaMapper mapper;
    private final JsonUtil jsonUtil;

    /* ---------------- CREATE ---------------- */
    @Override
    public SchemaResponseDTO create(SchemaRequestDTO request) {

        String userId = authService.getCurrentUserId();

        SchemaEntity entity = mapper.toEntity(request);
        entity.setUserId(userId);
        entity.setSource(SchemaSource.USER);

        SchemaEntity saved = schemaRepository.save(entity);

        updateOrderOnCreate(userId, saved.getId());

        return mapper.toDTO(saved);
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public SchemaResponseDTO update(String schemaId, SchemaRequestDTO request) {

        String userId = authService.getCurrentUserId();

        SchemaEntity existing = schemaRepository
                .findByIdAndUserId(schemaId, userId)
                .orElseThrow(() -> new SchemaNotFoundException("Schema not found"));

        if (existing.getSource() == SchemaSource.SYSTEM) {
            throw new SchemaOperationNotAllowedException("System schema cannot be updated");
        }

        SchemaEntity updated = mapper.toEntity(request);

        updated.setId(existing.getId());
        updated.setUserId(userId);
        updated.setSource(SchemaSource.USER);

        return mapper.toDTO(schemaRepository.save(updated));
    }

    /* ---------------- GET BY ID ---------------- */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public SchemaResponseDTO getById(String id) {

        String userId = authService.getCurrentUserId();

        // system
        SchemaResponseDTO system = systemRegistry.get(id);
        if (system != null) {
            return applyOverride(system, userId);
        }

        // user
        SchemaEntity entity = schemaRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new SchemaNotFoundException("Schema not found"));

        return mapper.toDTO(entity);
    }

    /* ---------------- GET ALL ---------------- */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Map<String, Object> getAll() {

        String userId = authService.getCurrentUserId();

        List<SchemaResponseDTO> result = new ArrayList<>();
        Map<String, SchemaResponseDTO> byId = new HashMap<>();

        // 1. system
        for (SchemaResponseDTO system : systemRegistry.getAll()) {
            SchemaResponseDTO merged = applyOverride(system, userId);
            result.add(merged);
            byId.put(merged.getId(), merged);
        }

        // 2. user
        List<SchemaEntity> userSchemas = schemaRepository.findAllByUserId(userId);

        for (SchemaEntity entity : userSchemas) {
            SchemaResponseDTO dto = mapper.toDTO(entity);
            result.add(dto);
            byId.put(dto.getId(), dto);
        }

        // 3. ORDER (IMPORTANT FIX)
        List<String> order = getUserOrder(userId);

        // fallback if empty
        if (order.isEmpty()) {
            order.addAll(systemRegistry.getOrder());
            order.addAll(userSchemas.stream().map(SchemaEntity::getId).toList());
        }

        return Map.of(
                "schemas", result,
                "schemasById", byId,
                "order", order
        );
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
    private SchemaResponseDTO applyOverride(SchemaResponseDTO system, String userId) {

        return overrideRepository
                .findByUserIdAndSchemaId(userId, system.getId())
                .map(override -> {

                    SchemaResponseDTO copy = mapper.copy(system);

                    try {
                        if (override.getDisplayJson() != null) {
                            copy.setDisplay(
                                    jsonUtil.fromJson(override.getDisplayJson(), DisplayDTO.class)
                            );
                        }

                        if (override.getColorRulesJson() != null) {
                            copy.setColorRules(
                                    jsonUtil.fromJsonList(override.getColorRulesJson(), ColorRuleDTO.class)
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