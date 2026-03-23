package com.example.find_my_edge.trade_setup.model;

import com.example.find_my_edge.common.enums.SemanticType;
import com.example.find_my_edge.trade_setup.enums.Tag;
import lombok.Data;

@Data
public class SetupField {

    private String id;

    private String mappedSchemaId;

    private String condition; // <= , >=, ==..., for categorical: "text contains", "text starts with"...

    private Double from;

    private Double to;

    private Object expected; // 100, 200, 300...,  or for categorical: "A", "B", "C"...

    private Tag tag;
}
