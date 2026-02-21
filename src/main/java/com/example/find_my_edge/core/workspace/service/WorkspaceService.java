package com.example.find_my_edge.core.workspace.service;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;

public interface WorkspaceService {
    PageDTO getPage(String page);

    PageDTO savePage(String page, PageDTO dto);

    PageDTO updatePage(String page, PageDTO dto);

    void deletePage(String page);
}
