package com.example.find_my_edge.common.auth.service.impl;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityCurrentUserService implements CurrentUserService {

    private Authentication getAuth() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication();
    }

    @Override
    public String getUserId() {
        Authentication auth = getAuth();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }

        return auth.getPrincipal().toString();
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