package com.example.find_my_edge.common.auth;

public class DevAuthService implements AuthService{
    @Override
    public String getCurrentUserId() {
        return "dev-user-123";
    }
}
