package com.example.find_my_edge.workspace.config.stat;

import com.example.find_my_edge.analytics.ast.util.HasDependencies;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.common.config.uiconfigs.ColorRuleConfig;
import com.example.find_my_edge.workspace.enums.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatConfig implements HasDependencies {
    private String id;
    private String title;
    private String type;
    private AstConfig ast;
    private String formula;
    private List<String> dependencies;
    private String format;
    private Source source;
    Double value;
    List<ColorRuleConfig> colorRules;
}
