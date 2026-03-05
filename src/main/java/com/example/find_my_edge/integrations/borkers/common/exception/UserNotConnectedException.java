package com.example.find_my_edge.integrations.borkers.common.exception;

public class UserNotConnectedException extends BrokerOAuthException {
    public UserNotConnectedException(String brokerName) {
        super("User has not connect to " + brokerName);
    }
}
