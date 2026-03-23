package com.example.find_my_edge.trade_setup.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.trade_setup.dto.*;
import com.example.find_my_edge.trade_setup.mapper.TradeSetupDtoMapper;
import com.example.find_my_edge.trade_setup.model.SetupField;
import com.example.find_my_edge.trade_setup.model.TradeSetup;
import com.example.find_my_edge.trade_setup.service.impl.TradeSetupServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trade-setups")
@RequiredArgsConstructor
public class TradeSetupController extends BaseController {

    private final TradeSetupServiceImpl service;
    private final TradeSetupDtoMapper mapper;

    /* -------- CREATE -------- */
    @PostMapping
    public ResponseEntity<ApiResponse<TradeSetupResponse>> create(
            @RequestBody TradeSetupRequest request) {
        TradeSetupResponse tradeSetupResponse = mapper.toResponse(service.create(request));
        return buildResponse(
                tradeSetupResponse,
                "Trade setup created successfully",
                HttpStatus.CREATED
        );
    }

    /* -------- GET ALL -------- */
    @GetMapping
    public ResponseEntity<ApiResponse<TradeSetupCollectionResponse>> getAll() {

        List<String> setupOrders = new ArrayList<>();
        Map<String, TradeSetupResponse> setups =
                service.getAll()
                       .stream()
                       .collect(Collectors.toMap(
                                        s -> {
                                            setupOrders.add(s.getId());
                                            return s.getId();
                                        }, mapper::toResponse
                                )
                       );

        return buildResponse(
                new TradeSetupCollectionResponse(setups, setupOrders),
                "Trade setups retrieved successfully"
        );
    }

    /* -------- GET BY ID -------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeSetupResponse>> getById(
            @PathVariable String id) {
        TradeSetupResponse byId = mapper.toResponse(service.getById(id));
        return buildResponse(byId, "Trade setup retrieved successfully");
    }

    /* -------- UPDATE -------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeSetupResponse>> update(
            @PathVariable String id,
            @RequestBody TradeSetupRequest request) {
        TradeSetupResponse update = mapper.toResponse(service.update(id, request));
        return buildResponse(update, "Trade setup updated successfully");
    }

    /* -------- DELETE -------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id) {

        service.delete(id);
        return buildNoContentResponse("Trade setup deleted successfully");
    }

    @PostMapping("/{setupId}/fields")
    public ResponseEntity<ApiResponse<SetupFieldResponse>> addField(
            @PathVariable String setupId,
            @RequestBody SetupFieldRequest request
    ) {

        SetupField model = mapper.toModel(request);

        SetupField field = service.addField(setupId, model);

        return buildResponse(mapper.toResponse(field), "Field added successfully");
    }

    @PutMapping("/{setupId}/fields/{fieldId}")
    public ResponseEntity<ApiResponse<TradeSetupResponse>> updateField(
            @PathVariable String setupId,
            @PathVariable String fieldId,
            @RequestBody SetupFieldRequest request
    ) {

        SetupField model = mapper.toModel(request);

        TradeSetupResponse response =
                mapper.toResponse(service.updateField(setupId, fieldId, model));

        return buildResponse(response, "Field updated successfully");
    }

    @DeleteMapping("/{setupId}/fields/{fieldId}")
    public ResponseEntity<ApiResponse<Void>> deleteField(
            @PathVariable String setupId,
            @PathVariable String fieldId
    ) {

        service.deleteField(setupId, fieldId);

        return buildNoContentResponse("Field deleted successfully");
    }
}