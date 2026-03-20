package com.example.find_my_edge.trade_setup.mapper;

import com.example.find_my_edge.trade_setup.dto.SetupFieldRequest;
import com.example.find_my_edge.trade_setup.dto.SetupFieldResponse;
import com.example.find_my_edge.trade_setup.dto.TradeSetupRequest;
import com.example.find_my_edge.trade_setup.dto.TradeSetupResponse;
import com.example.find_my_edge.trade_setup.model.SetupField;
import com.example.find_my_edge.trade_setup.model.TradeSetup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TradeSetupDtoMapper {

    /* ================= DTO → MODEL ================= */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    TradeSetup toModel(TradeSetupRequest dto);

    /* ================= MODEL → DTO ================= */

    TradeSetupResponse toResponse(TradeSetup model);

    /* ================= FIELD ================= */

    SetupField toModel(SetupFieldRequest dto);

    SetupFieldResponse toResponse(SetupField model);
}