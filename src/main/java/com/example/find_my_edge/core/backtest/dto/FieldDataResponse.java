package com.example.find_my_edge.core.backtest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FieldDataResponse {
    private Long id;
    private String label;
    private String type;
    private String value;
    private String mappedWith;
    private int mappedColumn;
    private List<String> options;
}
