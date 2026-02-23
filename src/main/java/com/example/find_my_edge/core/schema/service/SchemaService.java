package com.example.find_my_edge.core.schema.service;

import com.example.find_my_edge.core.schema.dto.SchemaResponseDTO;
import com.example.find_my_edge.core.schema.dto.SchemaRequestDTO;

import java.util.List;
import java.util.Map;

public interface SchemaService {

    SchemaResponseDTO create(SchemaRequestDTO request);

    SchemaResponseDTO update(String id, SchemaRequestDTO request);

    SchemaResponseDTO getById(String id);

    Map<String, Object> getAll();

    void delete(String id);

    List<String> updateOrder(List<String> order);

}
