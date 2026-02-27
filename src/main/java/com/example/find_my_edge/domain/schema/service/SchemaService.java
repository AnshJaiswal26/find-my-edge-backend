package com.example.find_my_edge.domain.schema.service;

import com.example.find_my_edge.domain.schema.enums.ViewType;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;

import java.util.List;

public interface SchemaService {

    Schema create(Schema schema);

    Schema update(String id, Schema schema);

    Schema getById(String id);

    SchemaBundle getAll();

    void delete(String id);

    List<String> updateOrder(List<String> order, ViewType viewType);
}
