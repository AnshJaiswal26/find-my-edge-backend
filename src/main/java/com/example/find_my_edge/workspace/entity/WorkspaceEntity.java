package com.example.find_my_edge.workspace.entity;

import com.example.find_my_edge.workspace.converter.WorkspaceDataConverter;
import com.example.find_my_edge.workspace.model.WorkspaceData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "workspace")
public class WorkspaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Convert(converter = WorkspaceDataConverter.class)
    @Column(columnDefinition = "JSON")
    private WorkspaceData data;
}
