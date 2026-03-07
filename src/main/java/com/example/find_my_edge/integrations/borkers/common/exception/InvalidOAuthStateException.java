package com.example.find_my_edge.integrations.borkers.common.exception;

public class InvalidOAuthStateException extends BrokerOAuthException{

    public InvalidOAuthStateException(String message) {
        super(message);
    }
}
