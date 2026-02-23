package com.example.find_my_edge.core.workspace.model;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WorkspaceData {
    private Map<String, PageDTO> pages = new HashMap<>();
}
