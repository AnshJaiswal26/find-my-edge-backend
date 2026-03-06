package com.example.find_my_edge.common.auth.service;


import java.util.UUID;

public interface CurrentUserService {

    UUID getUserId();

    boolean isAuthenticated();

    String getEmail();

    String getPlan();
}
