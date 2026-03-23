package com.example.find_my_edge.trade_setup.service;

import com.example.find_my_edge.trade_setup.dto.TradeSetupRequest;
import com.example.find_my_edge.trade_setup.entity.TradeSetupEntity;
import com.example.find_my_edge.trade_setup.model.SetupField;
import com.example.find_my_edge.trade_setup.model.TradeSetup;

import java.util.List;

public interface TradeSetupService {
    TradeSetup create(TradeSetupRequest dto);

    List<TradeSetup> getAll();

    TradeSetup getById(String setupId);

    TradeSetup update(String setupId, TradeSetupRequest dto);

    void delete(String setupId);

    TradeSetupEntity getOwnedReferenceOrThrow(String setupId);

    void updateFieldOrder(String setupId, List<String> newFieldOrder);

    // ================= FIELD MANAGEMENT =================
    SetupField addField(String setupId, SetupField model);

    TradeSetup updateField(
            String setupId,
            String fieldId,
            SetupField model
    );

    void deleteField(String setupId, String fieldId);
}
