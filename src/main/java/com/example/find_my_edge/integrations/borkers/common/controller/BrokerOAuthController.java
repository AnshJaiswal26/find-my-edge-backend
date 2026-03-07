package com.example.find_my_edge.integrations.borkers.common.controller;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.common.config.FrontendConfig;
import com.example.find_my_edge.integrations.borkers.common.dto.ConnectionStatusResponseDto;
import com.example.find_my_edge.integrations.borkers.common.factory.BrokerOAuthFactory;
import com.example.find_my_edge.integrations.borkers.common.service.BrokerOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class BrokerOAuthController {

    private final BrokerOAuthFactory brokerOAuthFactory;

    private final FrontendConfig frontendConfig;

    private final CurrentUserService currentUserService;


    @GetMapping("/{broker}/connect")
    public ResponseEntity<Map<String, String>> connect(
            @PathVariable String broker,
            HttpServletResponse response
    ) {

        BrokerOAuthService brokerOAuthService = brokerOAuthFactory.get(broker);

        UUID userId = currentUserService.getUserId();

        ResponseCookie cookie = ResponseCookie.from("oauth_user", userId.toString())
                                              .httpOnly(true)
                                              .maxAge(300)
                                              .path("/")
                                              .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String url = brokerOAuthService.generateConsentUrl();

        return ResponseEntity.ok(Map.of("url", url));
    }


    @GetMapping("/{broker}/callback")
    public ResponseEntity<String> callback(
            @PathVariable String broker,
            @RequestParam("tokenId") String tokenId,
            @CookieValue("oauth_user") String userId
    ) {

        BrokerOAuthService brokerOAuthService = brokerOAuthFactory.get(broker);

        brokerOAuthService.handleCallback(tokenId, UUID.fromString(userId));

        String url = frontendConfig.getUrl();

        // Redirect to frontend success page
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url + "/integrations/success/" + broker))
                .build();
    }


    @GetMapping("/{broker}/status")
    public ResponseEntity<ConnectionStatusResponseDto> status(@PathVariable String broker) {

        BrokerOAuthService brokerOAuthService = brokerOAuthFactory.get(broker);

        ConnectionStatusResponseDto status = brokerOAuthService.getConnectionStatus();

        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{broker}/disconnect")
    public ResponseEntity<ConnectionStatusResponseDto> disconnect(@PathVariable String broker) {

        BrokerOAuthService brokerOAuthService = brokerOAuthFactory.get(broker);

        ConnectionStatusResponseDto disconnect = brokerOAuthService.disconnect();

        return ResponseEntity.ok(disconnect);
    }
}