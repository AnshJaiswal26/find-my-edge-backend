package com.example.find_my_edge.api.schema.dto;

import com.example.find_my_edge.domain.schema.enums.ViewType;
import lombok.Data;

import java.util.List;

@Data
public class SchemaOrderRequestDto {

    private ViewType viewType;

    private List<String> order;
}
