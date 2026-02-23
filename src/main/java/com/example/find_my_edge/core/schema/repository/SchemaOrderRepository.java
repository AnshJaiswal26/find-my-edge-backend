package com.example.find_my_edge.core.schema.repository;

import com.example.find_my_edge.core.schema.entity.SchemaOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchemaOrderRepository extends JpaRepository<SchemaOrderEntity, String> {

    Optional<SchemaOrderEntity> findByUserId(String id);
}
