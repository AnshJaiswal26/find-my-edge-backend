package com.example.find_my_edge.integrations.borkers.common.repository;

import com.example.find_my_edge.integrations.borkers.common.entity.BrokerTokenEntity;
import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrokerTokenRepository extends JpaRepository<BrokerTokenEntity, Long> {
    Optional<BrokerTokenEntity> findByUserIdAndBroker(String userId, Broker broker);

    void deleteByUserIdAndBroker(String userId, Broker broker);
}