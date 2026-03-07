package com.example.find_my_edge.common.auth.controller;

import com.example.find_my_edge.common.auth.dto.AuthResponse;
import com.example.find_my_edge.common.auth.dto.LoginRequest;
import com.example.find_my_edge.common.auth.dto.RegisterRequest;
import com.example.find_my_edge.common.auth.dto.UserResponse;
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

        ResponseCookie cookie = buildRefreshCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // return only access token in response body
        return ResponseEntity.ok(
                new AuthResponse(authResponse.getAccessToken(), null)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {

        AuthResponse authResponse = authService.register(request);

        String refreshToken = authResponse.getRefreshToken();

        ResponseCookie cookie = buildRefreshCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                new AuthResponse(authResponse.getAccessToken(), null)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        AuthResponse authResponse = authService.refresh(refreshToken);

        String newRefreshToken = authResponse.getRefreshToken();

        ResponseCookie cookie = buildRefreshCookie(newRefreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                new AuthResponse(authResponse.getAccessToken(), null)
        );
    }

    private ResponseCookie buildRefreshCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                             .httpOnly(true)
                             .secure(false)
                             .path("/")
                             .maxAge(Duration.ofDays(7))
                             .sameSite("Lax")
                             .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {

        UserResponse user = authService.me();

        return ResponseEntity.ok(user);
    }
}