package com.example.find_my_edge.common.auth.service;


public interface CurrentUserService {

    String getUserId();

    boolean isAuthenticated();

    String getEmail();

    String getPlan();
}
