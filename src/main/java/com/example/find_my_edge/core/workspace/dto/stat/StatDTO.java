package com.example.find_my_edge.core.workspace.dto.stat;

import com.example.find_my_edge.common.dto.AstDTO;
import lombok.Data;

@Data
public class StatDTO {
    private String id;
    private String title;
    private String type;
    private AstDTO ast;
    private String format;
    long value;
}
