package com.example.find_my_edge.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AstDTO {

    private String type;   // "binary"| "key" | "constant" | "function"

    // for binary
    private String op;     // "+", "-", "*", "/"

    private AstDTO left;
    private AstDTO right;

    // for key
    private String key;

    // for constant
    private Double value;
    private String valueType; // "string" | "number"

    // for function
    private String fn;
    private List<AstDTO> args;
    private AstDTO arg;
}
