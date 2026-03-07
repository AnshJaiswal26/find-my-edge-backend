package com.example.find_my_edge.common.auth.exceptions;

public class UserAlreadyRegisteredException extends AuthenticationException {

    public UserAlreadyRegisteredException() {
        super("Account already exists with this email.");
    }

    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}