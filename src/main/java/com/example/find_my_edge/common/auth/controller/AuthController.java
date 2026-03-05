package com.example.find_my_edge.common.auth.controller;

import com.example.find_my_edge.common.auth.dto.AuthResponse;
import com.example.find_my_edge.common.auth.dto.LoginRequest;
import com.example.find_my_edge.common.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        AuthResponse authResponse = authService.login(request);

        String refreshToken = authResponse.getRefreshToken();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                                              .httpOnly(true)
                                              .secure(false) // change to true in production (HTTPS)
                                              .path("/auth/refresh")
                                              .maxAge(Duration.ofDays(7))
                                              .sameSite("Strict")
                                              .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // return only access token in response body
        return ResponseEntity.ok(
                new AuthResponse(authResponse.getAccessToken(), null)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {

        AuthResponse authResponse = authService.refresh(refreshToken);

        return ResponseEntity.ok(
                new AuthResponse(authResponse.getAccessToken(), null)
        );
    }
}