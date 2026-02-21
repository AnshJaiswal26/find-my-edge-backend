package com.example.find_my_edge.core.workspace.dto.chart;
import com.example.find_my_edge.common.dto.AstDTO;
import com.example.find_my_edge.common.dto.ColorRuleDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeriesConfigDTO {

    private String key;
    private String name;
    private String type; // number, string, etc
    private AstDTO ast;

    private List<ColorRuleDTO> colorRules = new ArrayList<>();

    private String color;
    private String markerColor;
    private String areaColor;
    private String label;

}