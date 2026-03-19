package com.example.find_my_edge.integrations.borkers.common.service;

import com.example.find_my_edge.integrations.borkers.common.dto.ConnectionStatusResponseDto;
import com.example.find_my_edge.integrations.borkers.common.entity.BrokerTokenEntity;
import com.example.find_my_edge.integrations.borkers.common.enums.Broker;

import java.util.UUID;

public interface BrokerOAuthService {

    Broker getBrokerName();

    String generateConsentUrl();

    void handleCallback(String tokenId, UUID userId);

    void validateToken(BrokerTokenEntity tokenEntity);

    ConnectionStatusResponseDto getConnectionStatus();

    ConnectionStatusResponseDto disconnect();

    String getValidToken();
}
