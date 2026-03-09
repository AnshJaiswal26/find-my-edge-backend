package com.example.find_my_edge.schema.service;

import com.example.find_my_edge.schema.dto.SchemaRequestDto;
import com.example.find_my_edge.schema.dto.SchemaResponseDtoBundle;
import com.example.find_my_edge.schema.dto.SchemaResponseDto;
import com.example.find_my_edge.schema.enums.ViewType;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaBundle;
import com.example.find_my_edge.schema.model.SchemaUpdate;

import java.util.List;

public interface SchemaService {

    Schema create(Schema schema);

    SchemaUpdate update(String id, Schema schema);

    Schema getById(String id);

    SchemaBundle getAll();

    void delete(String id);

    List<String> getOrder(ViewType viewType);

    List<String> updateOrder(List<String> order, ViewType viewType);
}
