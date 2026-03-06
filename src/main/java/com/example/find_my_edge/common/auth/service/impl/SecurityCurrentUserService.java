package com.example.find_my_edge.common.auth.service.impl;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityCurrentUserService implements CurrentUserService {

    private Authentication getAuth() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication();
    }

    @Override
    public UUID getUserId() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        return (UUID) auth.getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        Authentication auth = getAuth();
        return auth != null && auth.isAuthenticated();
    }

    @Override
    public String getEmail() {
        return null; // if stored in JWT claims
    }

    @Override
    public String getPlan() {
        return null; // if stored in JWT claims
    }

}