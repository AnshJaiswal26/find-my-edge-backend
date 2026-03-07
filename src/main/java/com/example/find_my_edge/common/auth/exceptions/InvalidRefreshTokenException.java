package com.example.find_my_edge.common.auth.exceptions;

public class InvalidRefreshTokenException extends AuthenticationException {
    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
