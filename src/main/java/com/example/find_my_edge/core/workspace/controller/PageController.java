package com.example.find_my_edge.core.workspace.controller;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.features.PageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workspace/{workspaceId}/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    @GetMapping("/{page}")
    public PageDTO getPage(@PathVariable Long workspaceId, @PathVariable String page) {
        return pageService.get(workspaceId, page);
    }
}