package com.example.find_my_edge.sheets.dto;

import lombok.Data;

import java.util.List;

@Data
public class SheetPayload {
    private String label;
    private Object value;
    private String type;
    private Integer mappedColumn;
    private List<String> options;
}