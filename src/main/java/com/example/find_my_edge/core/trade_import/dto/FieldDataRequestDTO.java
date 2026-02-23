package com.example.find_my_edge.core.trade_import.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FieldDataRequestDTO {
    String label;
    String type;
    String value;
    String mappedWith;
    int mappedColumn;
    List<String> options;
}