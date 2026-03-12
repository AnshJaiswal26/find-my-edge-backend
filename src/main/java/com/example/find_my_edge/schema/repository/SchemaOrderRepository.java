package com.example.find_my_edge.schema.repository;

import com.example.find_my_edge.schema.entity.SchemaOrderEntity;
import com.example.find_my_edge.schema.enums.ViewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchemaOrderRepository extends JpaRepository<SchemaOrderEntity, Long> {

    Optional<SchemaOrderEntity> findByUserIdAndViewType(UUID userId, ViewType viewType);

    List<SchemaOrderEntity> findAllByUserId(UUID userId);

}
