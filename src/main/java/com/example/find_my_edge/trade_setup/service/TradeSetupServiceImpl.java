package com.example.find_my_edge.trade_setup.service;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.trade_setup.dto.TradeSetupRequest;
import com.example.find_my_edge.trade_setup.dto.TradeSetupResponse;
import com.example.find_my_edge.trade_setup.entity.TradeSetupEntity;
import com.example.find_my_edge.trade_setup.repository.TradeSetupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeSetupServiceImpl {

    private final TradeSetupRepository repo;
    private final CurrentUserService currentUserService;

    /* ---------------- CREATE ---------------- */
    public TradeSetupResponse create(TradeSetupRequest dto) {

        UUID userId = currentUserService.getUserId();

        TradeSetupEntity setup = new TradeSetupEntity();

        setup.setName(dto.getName());
        setup.setImageUrl(dto.getImageUrl());
        setup.setRiskReward(dto.getRiskReward());
        setup.setIndicatorsUsed(dto.getIndicatorsUsed());
        setup.setUserId(userId);

        return mapToResponse(repo.save(setup));
    }

    /* ---------------- GET ALL ---------------- */
    @Transactional(readOnly = true)
    public List<TradeSetupResponse> getAllUserSetups() {

        UUID userId = currentUserService.getUserId();

        return repo.findByUserId(userId)
                   .stream()
                   .map(this::mapToResponse)
                   .toList();
    }

    /* ---------------- GET BY ID ---------------- */
    @Transactional(readOnly = true)
    public TradeSetupResponse getById(String setupId) {

        TradeSetupEntity setup = getOwnedSetupOrThrow(setupId);

        return mapToResponse(setup);
    }

    /* ---------------- UPDATE ---------------- */
    public TradeSetupResponse update(String setupId, TradeSetupRequest dto) {

        TradeSetupEntity setup = getOwnedSetupOrThrow(setupId);

        // partial update (safe)
        if (dto.getName() != null) setup.setName(dto.getName());
        if (dto.getImageUrl() != null) setup.setImageUrl(dto.getImageUrl());
        if (dto.getRiskReward() != null) setup.setRiskReward(dto.getRiskReward());
        if (dto.getIndicatorsUsed() != null) setup.setIndicatorsUsed(dto.getIndicatorsUsed());

        return mapToResponse(repo.save(setup));
    }

    /* ---------------- DELETE ---------------- */
    public void delete(String setupId) {

        TradeSetupEntity setup = getOwnedSetupOrThrow(setupId);

        repo.delete(setup);
    }

    /* ---------------- HELPER: OWNERSHIP CHECK ---------------- */
    private TradeSetupEntity getOwnedSetupOrThrow(String setupId) {

        UUID userId = currentUserService.getUserId();

        TradeSetupEntity setup = repo.findById(setupId)
                                     .orElseThrow(() -> new RuntimeException("Trade setup not found"));

        if (!setup.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to this trade setup");
        }

        return setup;
    }

    /* ---------------- MAPPER ---------------- */
    private TradeSetupResponse mapToResponse(TradeSetupEntity setup) {
        return TradeSetupResponse.builder()
                                 .id(setup.getId().toString())
                                 .name(setup.getName())
                                 .imageUrl(setup.getImageUrl())
                                 .riskReward(setup.getRiskReward())
                                 .indicatorsUsed(setup.getIndicatorsUsed())
                                 .build();
    }
}