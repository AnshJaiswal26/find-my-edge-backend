package com.example.find_my_edge.workspace.config.stat;

import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatConfig {
    private String id;
    private String title;
    private String type;
    private String formula;
    private AstConfig ast;
    private String format;
    Double value;
    List<ColorRuleConfig> colorRules;
}
