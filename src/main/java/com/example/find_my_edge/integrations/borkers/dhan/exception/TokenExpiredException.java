package com.example.find_my_edge.integrations.borkers.dhan.exception;

public class TokenExpiredException extends DhanOAuthException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
