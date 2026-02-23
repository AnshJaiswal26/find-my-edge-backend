package com.example.find_my_edge.core.schema.repository;

import com.example.find_my_edge.core.schema.entity.SchemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemaRepository extends JpaRepository<SchemaEntity, String> {

    Optional<SchemaEntity> findByIdAndUserId(String schemaId, String userId);

    List<SchemaEntity> findAllByUserId(String userId);

}