package com.example.find_my_edge.core.workspace.config;

import com.example.find_my_edge.core.workspace.model.WorkspaceData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkspaceDB {

    @Bean
    public WorkspaceData createWorkspace(){
        return  new WorkspaceData();
    }
}
