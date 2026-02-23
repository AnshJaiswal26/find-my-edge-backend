package com.example.find_my_edge.core.workspace.dto.stat;

import com.example.find_my_edge.common.dto.AstDTO;
import com.example.find_my_edge.common.dto.ColorRuleDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatDTO {
    private String id;
    private String title;
    private String type;
    private AstDTO ast;
    private String format;
    long value;
    List<ColorRuleDTO> colorRules;
}
