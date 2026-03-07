package com.example.find_my_edge.common.auth.service.impl;

import com.example.find_my_edge.common.auth.dto.AuthResponse;
import com.example.find_my_edge.common.auth.dto.LoginRequest;
import com.example.find_my_edge.common.auth.dto.RegisterRequest;
import com.example.find_my_edge.common.auth.dto.UserResponse;
import com.example.find_my_edge.common.auth.entity.RefreshToken;
import com.example.find_my_edge.common.auth.entity.User;
import com.example.find_my_edge.common.auth.exceptions.InvalidCredentialsException;
import com.example.find_my_edge.common.auth.exceptions.UserAlreadyRegisteredException;
import com.example.find_my_edge.common.auth.exceptions.UserNotAuthenticatedException;
import com.example.find_my_edge.common.auth.exceptions.UserNotFoundException;
import com.example.find_my_edge.common.auth.repository.UserRepository;
import com.example.find_my_edge.common.auth.service.AuthService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    private final WorkspaceService workspaceService;


    @Override
    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                                  .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest req) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new UserAlreadyRegisteredException();
        }

        User user = User.builder()
                        .email(req.getEmail())
                        .createdAt(Instant.now())
                        .username(req.getUsername())
                        .password(passwordEncoder.encode(req.getPassword()))
                        .build();

        User savedUser = userRepository.save(user);

        workspaceService.createDefaultWorkspace(savedUser.getId());

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = refreshTokenService.createToken(savedUser.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {

        // verify existing refresh token
        RefreshToken token = refreshTokenService.verify(refreshToken);

        // get user
        User user = userRepository.findById(token.getUserId())
                                  .orElseThrow(UserNotFoundException::new);

        // invalidate old refresh token (important for rotation)
        refreshTokenService.delete(user.getId());

        // create new refresh token
        String newRefreshToken = refreshTokenService.createToken(user.getId());

        // generate new access token
        String accessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(accessToken, newRefreshToken);
    }

    @Override
    public UserResponse me() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotAuthenticatedException();
        }

        UUID userId = (UUID) auth.getPrincipal();

        User user = userRepository.findById(userId)
                                  .orElseThrow(UserNotFoundException::new);

        return UserResponse.builder()
                           .id(user.getId().toString())
                           .email(user.getEmail())
                           .username(user.getUsername())
                           .build();
    }
}
