package com.example.find_my_edge.api.schema.controller;

import com.example.find_my_edge.api.schema.dto.SchemaResponseDTO;
import com.example.find_my_edge.api.schema.dto.SchemaResponseDTOBundle;
import com.example.find_my_edge.api.schema.mapper.SchemaDTOMapper;
import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.api.schema.dto.SchemaRequestDTO;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import com.example.find_my_edge.domain.schema.service.SchemaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
public class SchemaController extends BaseController {

    private final SchemaService schemaService;
    private final SchemaDTOMapper schemaDTOMapper;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<SchemaResponseDTO>> createSchema(@RequestBody SchemaRequestDTO request) {
        Schema schema = schemaService.create(schemaDTOMapper.toSchema(request));
        return buildResponse(schemaDTOMapper.toDTO(schema), "Schema created successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SchemaResponseDTO>> updateSchema(
            @PathVariable String id,
            @RequestBody SchemaRequestDTO request
    ) {
        Schema update = schemaService.update(id, schemaDTOMapper.toSchema(request));
        return buildResponse(schemaDTOMapper.toDTO(update), "Schema updated successfully");
    }

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<SchemaResponseDTOBundle>> getAllSchemas() {
        SchemaBundle all = schemaService.getAll();
        return buildResponse(schemaDTOMapper.toSchemaDTOBundle(all), "Schemas fetched successfully");
    }

    /* ---------------- GET BY ID ---------------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SchemaResponseDTO>> getSchemaById(@PathVariable String id) {
        Schema schema = schemaService.getById(id);
        return buildResponse(schemaDTOMapper.toDTO(schema), "Schema fetched successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSchema(@PathVariable String id) {
        schemaService.delete(id);
        return buildResponse(null, "Schema deleted successfully");
    }


    @PutMapping("/order")
    public ResponseEntity<ApiResponse<List<String>>> updateOrder(@RequestBody List<String> order) {

        List<String> newOrder = schemaService.updateOrder(order);
        return buildResponse(newOrder, "Schema order updated successfully");
    }
}
