package com.example.find_my_edge.core.workspace.controller;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping("/{page}")
    public PageDTO getPage(@PathVariable String page) {
        return workspaceService.getPage(page);
    }

    @PostMapping("/{page}")
    public PageDTO savePage(
            @PathVariable String page,
            @RequestBody PageDTO dto
    ) {
        return workspaceService.savePage(page, dto);
    }

    @DeleteMapping("/{page}")
    public void deletePage(@PathVariable String page) {
        workspaceService.deletePage(page);
    }
}
