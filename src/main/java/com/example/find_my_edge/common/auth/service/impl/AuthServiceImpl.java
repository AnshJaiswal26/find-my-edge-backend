package com.example.find_my_edge.common.auth.service.impl;

import com.example.find_my_edge.common.auth.dto.AuthResponse;
import com.example.find_my_edge.common.auth.dto.LoginRequest;
import com.example.find_my_edge.common.auth.entity.RefreshToken;
import com.example.find_my_edge.common.auth.entity.User;
import com.example.find_my_edge.common.auth.repository.UserRepository;
import com.example.find_my_edge.common.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                                  .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {

        RefreshToken token = refreshTokenService.verify(refreshToken);

        User user = userRepository.findById(token.getUserId())
                                  .orElseThrow();

        String accessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

}
