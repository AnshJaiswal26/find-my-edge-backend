package com.example.find_my_edge.integrations.borkers.common.repository;

import com.example.find_my_edge.integrations.borkers.common.entity.BrokerTokenEntity;
import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrokerTokenRepository extends JpaRepository<BrokerTokenEntity, Long> {
    Optional<BrokerTokenEntity> findByUserIdAndBroker(UUID userId, Broker broker);

    void deleteByUserIdAndBroker(UUID userId, Broker broker);
}