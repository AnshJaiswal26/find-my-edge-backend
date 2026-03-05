package com.example.find_my_edge.common.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}