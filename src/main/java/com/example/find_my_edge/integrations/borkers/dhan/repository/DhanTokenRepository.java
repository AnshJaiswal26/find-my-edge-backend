package com.example.find_my_edge.integrations.borkers.dhan.repository;

import com.example.find_my_edge.integrations.borkers.dhan.entity.DhanTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DhanTokenRepository extends JpaRepository<DhanTokenEntity, Long> {
    Optional<DhanTokenEntity> findByUserId(String userId);
}