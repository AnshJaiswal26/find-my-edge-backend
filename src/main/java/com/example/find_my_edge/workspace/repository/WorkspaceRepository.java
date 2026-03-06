package com.example.find_my_edge.workspace.repository;

import com.example.find_my_edge.workspace.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, Long> {
    Optional<WorkspaceEntity> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
