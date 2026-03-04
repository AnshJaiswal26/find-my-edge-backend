package com.example.find_my_edge.integrations.borkers.common.service;

import com.example.find_my_edge.integrations.borkers.common.dto.ConnectionStatusResponseDto;

import java.time.Instant;

public interface BrokerOAuthService {

    String getBrokerName();

    String generateConsentUrl();

    void handleCallback(String tokenId);

    String getValidToken();

    ConnectionStatusResponseDto getConnectionStatus();

    ConnectionStatusResponseDto disconnect();

    Instant getLastFetchedAt();

    void updateLastFetchedAt(Instant instant, String userId);
}
