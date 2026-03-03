package com.example.find_my_edge.integrations.borkers.dhan.service;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.integrations.borkers.dhan.config.DhanConfig;
import com.example.find_my_edge.integrations.borkers.dhan.dto.ConnectionStatusResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanAccessTokenResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanConsentResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.entity.DhanTokenEntity;
import com.example.find_my_edge.integrations.borkers.dhan.exception.FailedToConnect;
import com.example.find_my_edge.integrations.borkers.dhan.exception.TokenExpiredException;
import com.example.find_my_edge.integrations.borkers.dhan.exception.UserNotConnectedException;
import com.example.find_my_edge.integrations.borkers.dhan.repository.DhanTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DhanOAuthService {

    private final AuthService authService;

    private final DhanTokenRepository repo;
    private final RestClient restClient;
    private final DhanConfig config;

    public String generateConsentUrl() {

        DhanConsentResponseDto response =
                restClient.get()
                          .uri(config.getAuthUrl() + "/app/generate-consent?client_id=" + config.getClientId())
                          .headers((httpHeaders) -> {
                              httpHeaders.set("app_id", config.getAppId());
                              httpHeaders.set("app_secret", config.getAppSecret());
                          })
                          .retrieve()
                          .body(DhanConsentResponseDto.class);


        if (response == null || response.getConsentAppId() == null) {
            throw new FailedToConnect("Failed to generate consent from Dhan");
        }

        String consentAppId = response.getConsentAppId();

        return config.getAuthUrl() +
               "/login/consentApp-login?consentAppId=" + consentAppId;
    }

    public void handleCallback(String tokenId) {

        String userId = authService.getCurrentUserId();

        DhanAccessTokenResponseDto response =
                restClient.get()
                          .uri(config.getAuthUrl() + "/app/consumeApp-consent?tokenId=" + tokenId)
                          .headers((httpHeaders -> {
                              httpHeaders.set("app_id", config.getAppId());
                              httpHeaders.set("app_secret", config.getAppSecret());
                          }))
                          .retrieve()
                          .body(DhanAccessTokenResponseDto.class);

        if (response == null || response.getAccessToken() == null) {
            throw new FailedToConnect("Failed to fetch access token from Dhan");
        }

        String accessToken = response.getAccessToken();

        String expiryTimeStr = response.getExpiryTime();

        try {
            Instant expiry = LocalDateTime.parse(expiryTimeStr)
                                          .atZone(ZoneId.of("Asia/Kolkata"))
                                          .toInstant();

            DhanTokenEntity entity = repo.findByUserId(userId)
                                         .orElse(new DhanTokenEntity());

            entity.setUserId(userId);
            entity.setAccessToken(accessToken);
            entity.setExpiry(expiry);

            repo.save(entity);

        } catch (Exception e) {
            throw new FailedToConnect("Invalid expiry format from Dhan");
        }

    }

    public String getValidToken() {

        String userId = authService.getCurrentUserId();

        DhanTokenEntity token =
                repo.findByUserId(userId)
                    .orElseThrow(() -> new UserNotConnectedException("User not connected to dhan"));

        if (token.getExpiry().minusSeconds(60).isBefore(Instant.now())) {
            throw new TokenExpiredException("Access token is expired");
        }

        return token.getAccessToken();
    }

    public ApiResponse<Object> isConnected() {

        try {
            getValidToken();
        } catch (UserNotConnectedException e) {
            return ApiResponse.builder()
                              .httpStatus(HttpStatus.OK.value())
                              .state(ResponseState.NOT_CONNECTED)
                              .message(e.getMessage())
                              .build();
        } catch (TokenExpiredException e) {
            return ApiResponse.builder()
                              .httpStatus(HttpStatus.OK.value())
                              .state(ResponseState.TOKEN_EXPIRED)
                              .message(e.getMessage())
                              .build();
        }

        return ApiResponse.builder()
                          .httpStatus(HttpStatus.OK.value())
                          .state(ResponseState.CONNECTED)
                          .message("User is already connected to dhan")
                          .build();
    }

    public Instant getLastFetchedAt() {
        String userId = authService.getCurrentUserId();

        return repo.findByUserId(userId)
                   .map(DhanTokenEntity::getLastFetchedAt)
                   .orElse(null);
    }

    public void updateLastFetchedAt(Instant instant, String userId) {

        DhanTokenEntity token = repo.findByUserId(userId)
                                    .orElseThrow();

        token.setLastFetchedAt(instant);
        repo.save(token);
    }
}