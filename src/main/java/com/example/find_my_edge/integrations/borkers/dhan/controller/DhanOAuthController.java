package com.example.find_my_edge.integrations.borkers.dhan.controller;

import com.example.find_my_edge.config.FrontendConfig;
import com.example.find_my_edge.integrations.borkers.dhan.service.DhanOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/integrations/dhan")
@RequiredArgsConstructor
public class DhanOAuthController {

    private final DhanOAuthService dhanOAuthService;

    private final FrontendConfig frontendConfig;


    @GetMapping("/connect")
    public ResponseEntity<Void> connect() {

        String url = dhanOAuthService.generateConsentUrl();

        return ResponseEntity
                .status(302)
                .location(URI.create(url))
                .build();
    }


    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("tokenId") String tokenId) {

        dhanOAuthService.handleCallback(tokenId);

        String url = frontendConfig.getUrl();

        // Redirect to frontend success page
        return ResponseEntity
                .status(302)
                .location(URI.create(url + "/dhan/success"))
                .build();
    }


    @GetMapping("/status")
    public ResponseEntity<?> status() {

        boolean connected = dhanOAuthService.isConnected();

        return ResponseEntity.ok(Map.of(
                "connected", connected
        ));
    }
}