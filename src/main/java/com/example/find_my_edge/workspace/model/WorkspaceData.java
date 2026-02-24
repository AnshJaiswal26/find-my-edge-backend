package com.example.find_my_edge.workspace.model;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WorkspaceData {
    private Map<String, PageConfig> pages = new HashMap<>();
}
