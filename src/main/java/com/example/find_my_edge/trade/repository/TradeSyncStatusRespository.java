package com.example.find_my_edge.trade.repository;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.trade.entity.TradeSyncStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TradeSyncStatusRespository extends JpaRepository<TradeSyncStatusEntity, Long> {

    Optional<TradeSyncStatusEntity> findByUserIdAndBroker(UUID userId, Broker broker);
}
