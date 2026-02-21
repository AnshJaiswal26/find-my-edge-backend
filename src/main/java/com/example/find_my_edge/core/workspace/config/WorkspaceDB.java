package com.example.find_my_edge.core.workspace.config;

import com.example.find_my_edge.core.workspace.dto.core.WorkspaceDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkspaceDB {

    @Bean
    public WorkspaceDTO createWorkspace(){
        return  new WorkspaceDTO();
    }
}
