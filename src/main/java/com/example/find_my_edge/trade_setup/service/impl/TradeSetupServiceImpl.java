package com.example.find_my_edge.trade_setup.service.impl;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.common.storage.service.ImageStorageService;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.trade_setup.dto.TradeSetupRequest;
import com.example.find_my_edge.trade_setup.entity.SetupFieldEntity;
import com.example.find_my_edge.trade_setup.entity.TradeSetupEntity;
import com.example.find_my_edge.trade_setup.exception.TradeSetupFieldNotFoundException;
import com.example.find_my_edge.trade_setup.exception.TradeSetupNotFoundException;
import com.example.find_my_edge.trade_setup.exception.TradeSetupOrderMismatchException;
import com.example.find_my_edge.trade_setup.mapper.TradeSetupDtoMapper;
import com.example.find_my_edge.trade_setup.mapper.TradeSetupModelEntityMapper;
import com.example.find_my_edge.trade_setup.model.SetupField;
import com.example.find_my_edge.trade_setup.model.TradeSetup;
import com.example.find_my_edge.trade_setup.repository.TradeSetupRepository;
import com.example.find_my_edge.trade_setup.service.TradeSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeSetupServiceImpl implements TradeSetupService {

    private final TradeSetupRepository repo;
    private final CurrentUserService currentUserService;

    private final ImageStorageService imageStorageService;

    private final TradeSetupDtoMapper tradeSetupDtoMapper;

    private final TradeSetupModelEntityMapper setupModelEntityMapper;

    private final JsonUtil jsonUtil;

    /* ---------------- CREATE ---------------- */
    @Override
    public TradeSetup create(TradeSetupRequest dto) {

        TradeSetup model = tradeSetupDtoMapper.toModel(dto);

        TradeSetupEntity entity = setupModelEntityMapper.toEntity(model, new TradeSetupEntity());

        UUID userId = currentUserService.getUserId();
        entity.setUserId(userId);

        return setupModelEntityMapper.toModel(repo.save(entity));
    }

    /* ---------------- GET ALL ---------------- */
    @Transactional(readOnly = true)
    @Override
    public List<TradeSetup> getAll() {

        UUID userId = currentUserService.getUserId();

        return repo.findByUserId(userId).stream().map(setupModelEntityMapper::toModel).toList();
    }

    /* ---------------- GET BY ID ---------------- */
    @Transactional(readOnly = true)
    @Override
    public TradeSetup getById(String setupId) {

        TradeSetupEntity setup = getSetupOrThrow(setupId);

        return setupModelEntityMapper.toModel(setup);
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public TradeSetup update(String setupId, TradeSetupRequest dto) {

        TradeSetupEntity setup = getSetupOrThrow(setupId);

        // partial update (safe)
        if (dto.getName() != null) setup.setName(dto.getName());
        if (dto.getImageUrl() != null) setup.setImageUrl(dto.getImageUrl());
        if (dto.getImagePublicId() != null) {

            if (!dto.getImagePublicId().equals(setup.getImagePublicId())) {
                imageStorageService.delete(setup.getImagePublicId());
            }

            setup.setImagePublicId(dto.getImagePublicId());

        }

        return setupModelEntityMapper.toModel(repo.save(setup));
    }

    /* ---------------- DELETE ---------------- */
    @Override
    public void delete(String setupId) {

        TradeSetupEntity setup = getSetupOrThrow(setupId);

        if (setup.getImagePublicId() != null) {
            imageStorageService.delete(setup.getImagePublicId());
        }

        repo.delete(setup);
    }

    @Override
    public TradeSetupEntity getOwnedReferenceOrThrow(String setupId) {

        UUID userId = currentUserService.getUserId();

        boolean exists = repo.existsByIdAndUserId(setupId, userId);

        if (!exists) {
            throw new TradeSetupNotFoundException();
        }

        return repo.getReferenceById(setupId);
    }

    @Override
    public void updateFieldOrder(String setupId, List<String> newFieldOrder) {

        if (newFieldOrder.isEmpty()) return;

        UUID userId = currentUserService.getUserId();

        TradeSetupEntity setupEntity = repo.findByIdAndUserId(setupId, userId).orElseThrow(TradeSetupNotFoundException::new);

        if (setupEntity.getFields().size() != newFieldOrder.size()) {
            throw new TradeSetupOrderMismatchException("Field order size does not match the number of fields in the setup");
        }

        setupEntity.setFieldOrder(jsonUtil.toJsonList(newFieldOrder));

        repo.save(setupEntity);
    }

    /* ---------------- HELPER: OWNERSHIP CHECK ---------------- */
    private TradeSetupEntity getSetupOrThrow(String setupId) {

        UUID userId = currentUserService.getUserId();

        return repo.findByIdAndUserId(setupId, userId).orElseThrow(TradeSetupNotFoundException::new);
    }

    // ================= FIELD MANAGEMENT =================
    @Override
    public SetupField addField(String setupId, SetupField model) {

        TradeSetupEntity setup = getSetupOrThrow(setupId);

        SetupFieldEntity field = setupModelEntityMapper.toEntity(model);

        field.setTradeSetup(setup);

        setup.getFields().add(field);

        repo.save(setup);

        return setupModelEntityMapper.toModel(field);
    }

    @Override
    public TradeSetup updateField(String setupId, String fieldId, SetupField model) {

        TradeSetupEntity setup = getSetupOrThrow(setupId);

        SetupFieldEntity existingField = setup.getFields().stream().filter(f -> f.getId().equals(fieldId)).findFirst().orElseThrow(() -> new TradeSetupFieldNotFoundException("Field not found"));

        SetupField merged = setupModelEntityMapper.toModel(existingField);

        if (model.getMappedSchemaId() != null) merged.setMappedSchemaId(model.getMappedSchemaId());
        if (model.getCondition() != null) merged.setCondition(model.getCondition());
        if (model.getExpected() != null) merged.setExpected(model.getExpected());

        setupModelEntityMapper.updateEntityFromModel(model, existingField);

        return setupModelEntityMapper.toModel(repo.save(setup));
    }

    @Override
    public void deleteField(String setupId, String fieldId) {

        TradeSetupEntity setup = getSetupOrThrow(setupId);

        boolean removed = setup.getFields().removeIf(f -> f.getId().equals(fieldId));

        if (!removed) {
            throw new TradeSetupFieldNotFoundException("Field not found");
        }

        repo.save(setup);
    }

}