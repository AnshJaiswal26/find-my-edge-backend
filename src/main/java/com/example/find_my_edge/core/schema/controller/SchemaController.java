package com.example.find_my_edge.core.schema.controller;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.schema.dto.SchemaRequest;
import com.example.find_my_edge.core.schema.service.SchemaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
public class SchemaController {

    private final SchemaService schemaService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createSchema(@RequestBody SchemaRequest request) {
        Map<String, Object> created = schemaService.create(request);
        return buildResponse(created, "Schema created successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateSchema(
            @PathVariable String id,
            @RequestBody SchemaRequest request
    ) {
        request.setId(id);
        Map<String, Object> updated = schemaService.update(request);
        return buildResponse(updated, "Schema updated successfully");
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
        SchemaRequest schema = schemaService.getById(id);
        return buildResponse(schema, "Schema fetched successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSchema(@PathVariable String id) {
        Map<String, Object> deleted = schemaService.delete(id);
        return buildResponse(deleted, "Schema deleted successfully");
    }


    @PutMapping("/order")
    public ResponseEntity<ApiResponse<Object>> updateOrder(@RequestBody List<String> order) {

        List<String> newOrder = schemaService.updateOrder(order);
        return buildResponse(newOrder, "Schema order updated successfully");
    }

    /* ---------------- COMMON BUILDER ---------------- */
    private ResponseEntity<ApiResponse<Object>> buildResponse(Object data, String message) {

        boolean isList = data instanceof List;
        int count = isList ? ((List<?>) data).size() : 1;
        boolean empty = data == null || (isList && ((List<?>) data).isEmpty());

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .state(ResponseState.SUCCESS)
                           .httpStatus(HttpStatus.OK.value())
                           .message(message)
                           .data(data)
                           .meta(Map.of("empty", empty, "count", count))
                           .build()
        );
    }
}
