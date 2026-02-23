package com.example.find_my_edge.common.dto;

import lombok.Data;

@Data
public class FilterDTO {
    private String key;
    private String operator;
    private Double value;
    private Double from;
    private Double to;
}
