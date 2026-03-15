package com.example.find_my_edge.schema.dto;

import com.example.find_my_edge.schema.enums.ViewType;
import lombok.Data;

import java.util.List;

@Data
public class SchemaOrderRequest {

    private ViewType viewType;

    private List<String> order;
}
