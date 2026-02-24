package com.example.find_my_edge.workspace.config.stat;

import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatConfig {
    private String id;
    private String title;
    private String type;
    private AstConfig ast;
    private String format;
    long value;
    List<ColorRuleConfig> colorRules;
}
