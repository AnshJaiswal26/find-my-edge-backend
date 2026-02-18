package com.example.find_my_edge.core.schema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpressionDto {

    private String type;   // "binary", "key", "constant", "function"

    // for binary
    private String op;     // +, -, *, /

    private ExpressionDto left;
    private ExpressionDto right;

    // for key
    private String key;

    // for constant
    private Double value;

    // for function
    private String fn;
    private List<ExpressionDto> args;
}
