package com.example.find_my_edge.common.auth.service;

import com.example.find_my_edge.common.auth.dto.AuthResponse;
import com.example.find_my_edge.common.auth.dto.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest req);

    AuthResponse refresh(String refreshToken);
}
