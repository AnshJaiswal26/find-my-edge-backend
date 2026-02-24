package com.example.find_my_edge.api.workspace.controller;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.features.PageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workspace/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    @GetMapping("/{page}")
    public PageConfig getPage(@PathVariable String page) {
        return pageService.get(page);
    }
}