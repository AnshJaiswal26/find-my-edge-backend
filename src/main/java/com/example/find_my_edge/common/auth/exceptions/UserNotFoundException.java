package com.example.find_my_edge.common.auth.exceptions;

public class UserNotFoundException extends AuthenticationException {

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}