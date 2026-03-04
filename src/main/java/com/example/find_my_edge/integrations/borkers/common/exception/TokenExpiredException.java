package com.example.find_my_edge.integrations.borkers.common.exception;

public class TokenExpiredException extends BrokerOAuthException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
