package com.example.find_my_edge.trade_setup.model;

import lombok.Data;

@Data
public class FieldMatch {
    private String fieldId;
    private Object actualValue;
    private Object expectedValue;
    private boolean isMatch;
}
