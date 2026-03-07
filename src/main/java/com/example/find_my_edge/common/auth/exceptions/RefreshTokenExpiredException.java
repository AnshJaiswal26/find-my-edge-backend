package com.example.find_my_edge.common.auth.exceptions;

public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException(){
        super("Refresh token expired");
    }
}
