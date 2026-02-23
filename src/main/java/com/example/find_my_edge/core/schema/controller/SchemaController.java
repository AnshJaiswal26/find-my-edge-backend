package com.example.find_my_edge.core.schema.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.schema.dto.SchemaResponseDTO;
import com.example.find_my_edge.core.schema.dto.SchemaRequestDTO;
import com.example.find_my_edge.core.schema.service.SchemaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
public class SchemaController extends BaseController {

    private final SchemaService schemaService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createSchema(@RequestBody SchemaRequestDTO request) {
        SchemaResponseDTO schemaResponseDTO = schemaService.create(request);
        return buildResponse(schemaResponseDTO, "Schema created successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateSchema(
            @PathVariable String id,
            @RequestBody SchemaRequestDTO request
    ) {
        SchemaResponseDTO update = schemaService.update(id, request);
        return buildResponse(update, "Schema updated successfully");
    }

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllSchemas() {
        Map<String, Object> list = schemaService.getAll();
        return buildResponse(list, "Schemas fetched successfully");
    }

    /* ---------------- GET BY ID ---------------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getSchemaById(@PathVariable String id) {
        SchemaResponseDTO schema = schemaService.getById(id);
        return buildResponse(schema, "Schema fetched successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSchema(@PathVariable String id) {
        schemaService.delete(id);
        return buildResponse(null, "Schema deleted successfully");
    }


    @PutMapping("/order")
    public ResponseEntity<ApiResponse<Object>> updateOrder(@RequestBody List<String> order) {

        List<String> newOrder = schemaService.updateOrder(order);
        return buildResponse(newOrder, "Schema order updated successfully");
    }
}
