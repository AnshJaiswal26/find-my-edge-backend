package com.example.find_my_edge.schema.repository;

import com.example.find_my_edge.schema.entity.SchemaOrderEntity;
import com.example.find_my_edge.schema.enums.ViewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchemaOrderRepository extends JpaRepository<SchemaOrderEntity, Long> {

    Optional<SchemaOrderEntity> findByUserIdAndViewType(String userId, ViewType viewType);
}
