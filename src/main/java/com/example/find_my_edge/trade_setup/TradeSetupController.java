package com.example.find_my_edge.trade_setup;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.trade_setup.dto.TradeSetupRequest;
import com.example.find_my_edge.trade_setup.dto.TradeSetupResponse;
import com.example.find_my_edge.trade_setup.service.TradeSetupServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade-setups")
@RequiredArgsConstructor
public class TradeSetupController extends BaseController {

    private final TradeSetupServiceImpl service;

    /* -------- CREATE -------- */
    @PostMapping
    public ResponseEntity<ApiResponse<TradeSetupResponse>> create(
            @RequestBody TradeSetupRequest request) {
        TradeSetupResponse tradeSetupResponse = service.create(request);
        return buildResponse(
                tradeSetupResponse,
                "Trade setup created successfully",
                HttpStatus.CREATED
        );
    }

    /* -------- GET ALL -------- */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TradeSetupResponse>>> getAll() {
        List<TradeSetupResponse> setups = service.getAllUserSetups();
        return buildResponse(setups, "Trade setups retrieved successfully");
    }

    /* -------- GET BY ID -------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeSetupResponse>> getById(
            @PathVariable String id) {
        TradeSetupResponse byId = service.getById(id);
        return buildResponse(byId, "Trade setup retrieved successfully");
    }

    /* -------- UPDATE -------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeSetupResponse>> update(
            @PathVariable String id,
            @RequestBody TradeSetupRequest request) {
        TradeSetupResponse update = service.update(id, request);
        return buildResponse(update, "Trade setup updated successfully");
    }

    /* -------- DELETE -------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id) {

        service.delete(id);
        return buildNoContentResponse("Trade setup deleted successfully");
    }
}