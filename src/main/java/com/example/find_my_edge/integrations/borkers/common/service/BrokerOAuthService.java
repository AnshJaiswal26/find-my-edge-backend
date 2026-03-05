package com.example.find_my_edge.integrations.borkers.common.service;

import com.example.find_my_edge.integrations.borkers.common.dto.ConnectionStatusResponseDto;
import com.example.find_my_edge.integrations.borkers.common.entity.BrokerTokenEntity;

import java.time.Instant;

public interface BrokerOAuthService {

    String getBrokerName();

    String generateConsentUrl();

    void handleCallback(String tokenId);

    void validateToken(BrokerTokenEntity tokenEntity);

    ConnectionStatusResponseDto getConnectionStatus();

    ConnectionStatusResponseDto disconnect();

    Instant getLastFetchedAt();

    void updateLastFetchedAt(Instant instant, String userId);

    String getValidToken();
}
