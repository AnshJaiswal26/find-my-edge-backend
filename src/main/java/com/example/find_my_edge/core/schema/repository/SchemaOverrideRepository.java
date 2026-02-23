package com.example.find_my_edge.core.schema.repository;

import com.example.find_my_edge.core.schema.entity.SchemaOrderEntity;
import com.example.find_my_edge.core.schema.entity.SchemaOverrideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchemaOverrideRepository extends JpaRepository<SchemaOverrideEntity, String> {

    Optional<SchemaOverrideEntity> findByUserIdAndSchemaId(String userId, String schemaId);

}