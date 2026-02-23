package com.example.find_my_edge.core.workspace.entity;

import com.example.find_my_edge.core.workspace.converter.WorkspaceDataConverter;
import com.example.find_my_edge.core.workspace.model.WorkspaceData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Convert(converter = WorkspaceDataConverter.class)
    @Column(columnDefinition = "JSON")
    private WorkspaceData data;
}
