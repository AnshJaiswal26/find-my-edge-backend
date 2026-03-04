package com.example.find_my_edge.integrations.borkers.common.controller;

import com.example.find_my_edge.config.FrontendConfig;
import com.example.find_my_edge.integrations.borkers.common.dto.ConnectionStatusResponseDto;
import com.example.find_my_edge.integrations.borkers.common.factory.BrokerOAuthFactory;
import com.example.find_my_edge.integrations.borkers.common.service.BrokerOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class BrokerOAuthController {

    private final BrokerOAuthFactory brokerOAuthFactory;

    private final FrontendConfig frontendConfig;


    @GetMapping("/{broker}/connect")
    public ResponseEntity<Void> connect(@PathVariable String broker) {

        BrokerOAuthService brokerOAuthService = brokerOAuthFactory.get(broker);

        String url = brokerOAuthService.generateConsentUrl();

        return ResponseEntity
                .status(302)
                .location(URI.create(url))
                .build();
    }


    @GetMapping("/{broker}/callback")
    public ResponseEntity<String> callback(
            @PathVariable String broker,
            @RequestParam("tokenId") String tokenId) {

        BrokerOAuthService brokerOAuthService = brokerOAuthFactory.get(broker);

        brokerOAuthService.handleCallback(tokenId);

        String url = frontendConfig.getUrl();

        // Redirect to frontend success page
        return ResponseEntity
                .status(302)
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