package com.example.find_my_edge.core.workspace.features;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;

public interface PageService {
    PageDTO get(Long workspaceId, String page);
}
