package com.example.find_my_edge.core.workspace.dto.core;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WorkspaceDTO {
    private Map<String, PageDTO> pages = new HashMap<>();
}
