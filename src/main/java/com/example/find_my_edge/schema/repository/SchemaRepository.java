package com.example.find_my_edge.schema.repository;

import com.example.find_my_edge.schema.entity.SchemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchemaRepository extends JpaRepository<SchemaEntity, String> {

    Optional<SchemaEntity> findByIdAndUserId(String schemaId, UUID userId);

    List<SchemaEntity> findAllByUserId(UUID userId);

}