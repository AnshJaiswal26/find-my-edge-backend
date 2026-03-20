package com.example.find_my_edge.trade_setup.repository;

import com.example.find_my_edge.trade_setup.entity.TradeSetupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradeSetupRepository extends JpaRepository<TradeSetupEntity, String> {

    List<TradeSetupEntity> findByUserId(UUID userId);

    boolean existsByIdAndUserId(String setupId, UUID userId);

    Optional<TradeSetupEntity> findByIdAndUserId(String setupId, UUID userId);
}