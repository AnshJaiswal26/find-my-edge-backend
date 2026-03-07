package com.example.find_my_edge.integrations.borkers.dhan.service;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.integrations.borkers.common.entity.BrokerTokenEntity;
import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.integrations.borkers.dhan.config.DhanConfig;
import com.example.find_my_edge.integrations.borkers.common.dto.ConnectionStatusResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanAccessTokenResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanConsentResponseDto;
import com.example.find_my_edge.integrations.borkers.common.exception.FailedToConnectException;
import com.example.find_my_edge.integrations.borkers.common.exception.TokenExpiredException;
import com.example.find_my_edge.integrations.borkers.common.exception.UserNotConnectedException;
import com.example.find_my_edge.integrations.borkers.common.repository.BrokerTokenRepository;
import com.example.find_my_edge.integrations.borkers.common.enums.ConnectionStatus;
import com.example.find_my_edge.integrations.borkers.common.service.BrokerOAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DhanOAuthService implements BrokerOAuthService {

    private final CurrentUserService currentUserService;

    private final BrokerTokenRepository repo;
    private final RestClient restClient;
    private final DhanConfig config;

    private void setHeaders(HttpHeaders httpHeaders) {
        httpHeaders.set("app_id", config.getAppId());
        httpHeaders.set("app_secret", config.getAppSecret());
    }

    @Override
    public String getBrokerName() {
        return Broker.DHAN.name().toLowerCase();
    }

    @Override
    public String generateConsentUrl() {

        DhanConsentResponseDto response =
                restClient.get()
                          .uri(config.getAuthUrl() + "/app/generate-consent?client_id=" + config.getClientId())
                          .headers(this::setHeaders)
                          .retrieve()
                          .body(DhanConsentResponseDto.class);


        if (response == null || response.getConsentAppId() == null) {
            throw new FailedToConnectException("Failed to generate consent from Dhan");
        }

        String consentAppId = response.getConsentAppId();

        return config.getAuthUrl() +
               "/login/consentApp-login?consentAppId=" + consentAppId;
    }

    @Transactional
    @Override
    public void handleCallback(String tokenId, UUID userId) {

        DhanAccessTokenResponseDto response =
                restClient.get()
                          .uri(config.getAuthUrl() + "/app/consumeApp-consent?tokenId=" + tokenId)
                          .headers(this::setHeaders)
                          .retrieve()
                          .body(DhanAccessTokenResponseDto.class);

        if (response == null || response.getAccessToken() == null) {
            throw new FailedToConnectException("Failed to fetch access token from Dhan");
        }

        try {
            String accessToken = response.getAccessToken();
            String expiryTimeStr = response.getExpiryTime().trim();

            Instant expiry = LocalDateTime.parse(expiryTimeStr)
                                          .atZone(ZoneId.of("Asia/Kolkata"))
                                          .toInstant();

            BrokerTokenEntity entity = repo.findByUserIdAndBroker(userId, Broker.DHAN)
                                           .orElse(new BrokerTokenEntity());

            entity.setUserId(userId);
            entity.setBroker(Broker.DHAN);
            entity.setStatus(ConnectionStatus.CONNECTED);
            entity.setAccessToken(accessToken);
            entity.setExpiry(expiry);
            entity.setConnectedAt(Instant.now());

            repo.save(entity);

        } catch (Exception e) {
            throw new FailedToConnectException("Unexpected expiry format from Dhan");
        }

    }

    @Override
    public void validateToken(BrokerTokenEntity tokenEntity) {

        if (
                tokenEntity.getExpiry()
                           .minusSeconds(60)
                           .isBefore(Instant.now())
        ) {
            throw new TokenExpiredException("Access token is expired for dhan");
        }

    }

    @Override
    public ConnectionStatusResponseDto getConnectionStatus() {

        UUID userId = currentUserService.getUserId();

        BrokerTokenEntity tokenEntity =
                repo.findByUserIdAndBroker(userId, Broker.DHAN)
                    .orElse(null);

        // User never connected
        if (tokenEntity == null) {
            return ConnectionStatusResponseDto
                    .builder()
                    .status(ConnectionStatus.NOT_CONNECTED)
                    .connectedAt(null)
                    .expiresOn(null)
                    .message("User has not connected to dhan")
                    .build();
        }

        //  Check if user disconnected
        if (tokenEntity.getStatus() == ConnectionStatus.DISCONNECTED) {

            return ConnectionStatusResponseDto
                    .builder()
                    .status(ConnectionStatus.DISCONNECTED)
                    .connectedAt(formatTime(tokenEntity.getConnectedAt()))
                    .expiresOn(formatTime(tokenEntity.getExpiry()))
                    .message("User disconnected from dhan")
                    .build();
        }

        //  Validate token expiry
        try {
            validateToken(tokenEntity);
        } catch (TokenExpiredException e) {

            if (tokenEntity.getStatus() != ConnectionStatus.TOKEN_EXPIRED) {
                tokenEntity.setStatus(ConnectionStatus.TOKEN_EXPIRED);
                repo.save(tokenEntity);
            }

            return ConnectionStatusResponseDto
                    .builder()
                    .status(ConnectionStatus.TOKEN_EXPIRED)
                    .connectedAt(formatTime(tokenEntity.getConnectedAt()))
                    .expiresOn(formatTime(tokenEntity.getExpiry()))
                    .message(e.getMessage())
                    .build();
        }

        return ConnectionStatusResponseDto
                .builder()
                .status(ConnectionStatus.CONNECTED)
                .connectedAt(formatTime(tokenEntity.getConnectedAt()))
                .expiresOn(formatTime(tokenEntity.getExpiry()))
                .message("User is connected to dhan")
                .build();

    }

    @Transactional
    @Override
    public ConnectionStatusResponseDto disconnect() {
        UUID userId = currentUserService.getUserId();

        BrokerTokenEntity entity =
                repo.findByUserIdAndBroker(userId, Broker.DHAN)
                    .orElseThrow(() -> new UserNotConnectedException(Broker.DHAN.name()));


        entity.setStatus(ConnectionStatus.DISCONNECTED);

        entity.setAccessToken("");
        entity.setExpiry(null);


        return ConnectionStatusResponseDto
                .builder()
                .status(ConnectionStatus.DISCONNECTED)
                .connectedAt(formatTime(entity.getConnectedAt()))
                .expiresOn(formatTime(entity.getExpiry()))
                .message("User Disconnected from dhan")
                .build();

    }

    @Override
    public Instant getLastFetchedAt() {
        UUID userId = currentUserService.getUserId();

        return repo.findByUserIdAndBroker(userId, Broker.DHAN)
                   .map(BrokerTokenEntity::getLastFetchedAt)
                   .orElse(null);
    }

    @Override
    public void updateLastFetchedAt(Instant instant, UUID userId) {

        BrokerTokenEntity token =
                repo.findByUserIdAndBroker(userId, Broker.DHAN)
                    .orElseThrow(() -> new UserNotConnectedException(Broker.DHAN.name()));

        token.setLastFetchedAt(instant);
        repo.save(token);
    }

    @Override
    public String getValidToken() {

        UUID userId = currentUserService.getUserId();

        BrokerTokenEntity token =
                repo.findByUserIdAndBroker(userId, Broker.DHAN)
                    .orElseThrow(() -> new UserNotConnectedException(Broker.DHAN.name()));

        validateToken(token);

        return token.getAccessToken();
    }

    private String formatTime(Instant instant) {
        if (instant == null) return null;
        return instant
                .atZone(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }
}