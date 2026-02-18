package com.example.find_my_edge.core.schema.service;

import com.example.find_my_edge.core.schema.dto.SchemaRequest;

import java.util.List;
import java.util.Map;

public interface SchemaService {

    Map<String, Object> create(SchemaRequest request);

    Map<String, Object> update(SchemaRequest request);

    SchemaRequest getById(String id);

    Map<String, Object> getAll();

    Map<String, Object> delete(String id);

    List<String> updateOrder(List<String> order);

    void seed(SchemaRequest schema);
}
