package com.example.find_my_edge.common.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GroupSpecDTO {

    private String type;
    private String key;

    private String unit;
    private List<RangeDTO> ranges;

    private String operator;
    private double value;
    private double valueTo;
    private Map<String, String> labels;

    private AstDTO ast;

}
