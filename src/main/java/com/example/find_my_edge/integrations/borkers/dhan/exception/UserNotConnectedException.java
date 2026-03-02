package com.example.find_my_edge.integrations.borkers.dhan.exception;

public class UserNotConnectedException extends DhanOAuthException {
    public UserNotConnectedException(String message) {
        super(message);
    }
}
