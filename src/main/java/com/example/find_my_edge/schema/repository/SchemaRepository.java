package com.example.find_my_edge.schema.repository;

import com.example.find_my_edge.common.enums.SemanticType;
import com.example.find_my_edge.schema.entity.SchemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchemaRepository extends JpaRepository<SchemaEntity, String> {

    Optional<SchemaEntity> findByIdAndUserId(String schemaId, UUID userId);

    List<SchemaEntity> findAllByUserId(UUID userId);

    @Query("""
                select s.label
                from SchemaEntity s
                join s.dependencies d
                where s.userId = :userId
                and d = :schemaId
            """)
    List<String> findDependentSchemaLabels(
            @Param("userId") UUID userId,
            @Param("schemaId") String schemaId
    );

    @Query("SELECT s.semanticType FROM SchemaEntity s WHERE s.id = :id")
    SemanticType findSemanticTypeById(@Param("id") String id);

    boolean existsByUserIdAndLabel(UUID userId, String label);
}