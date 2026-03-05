package com.example.find_my_edge.integrations.borkers.common.exception;

public class FailedToConnectException extends BrokerOAuthException {
    public FailedToConnectException(String message) {
        super(message);
    }
}
