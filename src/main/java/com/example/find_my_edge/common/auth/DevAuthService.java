package com.example.find_my_edge.common.auth;


import org.springframework.stereotype.Service;

@Service
public class DevAuthService implements AuthService{
    @Override
    public String getCurrentUserId() {
        return "dev-user-123";
    }
}
