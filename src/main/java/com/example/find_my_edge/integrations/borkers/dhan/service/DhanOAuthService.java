package com.example.find_my_edge.integrations.borkers.dhan.service;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.integrations.borkers.common.entity.BrokerTokenEntity;
import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import com.example.find_my_edge.integrations.borkers.dhan.config.DhanConfig;
import com.example.find_my_edge.integrations.borkers.common.dto.ConnectionStatusResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanAccessTokenResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanConsentResponseDto;
import com.example.find_my_edge.integrations.borkers.common.exception.FailedToConnect;
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

@Service
@RequiredArgsConstructor
public class DhanOAuthService implements BrokerOAuthService {

    private final AuthService authService;

    private final BrokerTokenRepository repo;
    private final RestClient restClient;
    private final DhanConfig config;

    private void setHeaders(HttpHeaders httpHeaders) {
        httpHeaders.set("app_id", config.getAppId());
        httpHeaders.set("app_secret", config.getAppSecret());
    }

    @Override
    public String getBrokerName() {
        return "dhan";
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
            throw new FailedToConnect("Failed to generate consent from Dhan");
        }

        String consentAppId = response.getConsentAppId();

        return config.getAuthUrl() +
               "/login/consentApp-login?consentAppId=" + consentAppId;
    }

    @Transactional
    @Override
    public void handleCallback(String tokenId) {

        String userId = authService.getCurrentUserId();

        DhanAccessTokenResponseDto response =
                restClient.get()
                          .uri(config.getAuthUrl() + "/app/consumeApp-consent?tokenId=" + tokenId)
                          .headers(this::setHeaders)
                          .retrieve()
                          .body(DhanAccessTokenResponseDto.class);

        if (response == null || response.getAccessToken() == null) {
            throw new FailedToConnect("Failed to fetch access token from Dhan");
        }

        System.out.println("expiry raw = [" + response.getExpiryTime() + "]");

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
            entity.setAccessToken(accessToken);
            entity.setExpiry(expiry);

            repo.save(entity);

        } catch (Exception e) {
            throw new FailedToConnect("Invalid expiry format from Dhan");
        }

    }

    @Override
    public String getValidToken() {

        String userId = authService.getCurrentUserId();

        BrokerTokenEntity token =
                repo.findByUserIdAndBroker(userId, Broker.DHAN)
                    .orElseThrow(() -> new UserNotConnectedException("User not connected to dhan"));

        if (token.getExpiry().minusSeconds(60).isBefore(Instant.now())) {
            throw new TokenExpiredException("Access token is expired for dhan");
        }

        return token.getAccessToken();
    }

    @Override
    public ConnectionStatusResponseDto getConnectionStatus() {

        try {
            getValidToken();
        } catch (UserNotConnectedException e) {
            return new ConnectionStatusResponseDto(
                    ConnectionStatus.CONNECTED,
                    e.getMessage()
            );
        } catch (TokenExpiredException e) {
            return new ConnectionStatusResponseDto(
                    ConnectionStatus.TOKEN_EXPIRED,
                    e.getMessage()
            );
        }

        return new ConnectionStatusResponseDto(
                ConnectionStatus.CONNECTED,
                "User is already connected to dhan"
        );
    }

    @Transactional
    @Override
    public ConnectionStatusResponseDto disconnect() {
        String userId = authService.getCurrentUserId();

        repo.deleteByUserIdAndBroker(userId, Broker.DHAN);

        return new ConnectionStatusResponseDto(
                ConnectionStatus.DISCONNECTED,
                "User Disconnected from dhan"
        );
    }

    @Override
    public Instant getLastFetchedAt() {
        String userId = authService.getCurrentUserId();

        return repo.findByUserIdAndBroker(userId, Broker.DHAN)
                   .map(BrokerTokenEntity::getLastFetchedAt)
                   .orElse(null);
    }

    @Override
    public void updateLastFetchedAt(Instant instant, String userId) {

        BrokerTokenEntity token =
                repo.findByUserIdAndBroker(userId, Broker.DHAN)
                    .orElseThrow(() -> new UserNotConnectedException("User is not connected to dhan"));

        token.setLastFetchedAt(instant);
        repo.save(token);
    }
}