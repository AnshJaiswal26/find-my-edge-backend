package com.example.find_my_edge.common.auth.service;

import com.example.find_my_edge.common.auth.dto.AuthResponse;
import com.example.find_my_edge.common.auth.dto.LoginRequest;
import com.example.find_my_edge.common.auth.dto.RegisterRequest;
import com.example.find_my_edge.common.auth.dto.UserResponse;

public interface AuthService {

    AuthResponse login(LoginRequest req);

    AuthResponse register(RegisterRequest req);

    AuthResponse refresh(String refreshToken);

    UserResponse me();
}
