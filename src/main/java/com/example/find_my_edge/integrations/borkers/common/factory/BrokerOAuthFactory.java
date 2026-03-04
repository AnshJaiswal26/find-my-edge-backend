package com.example.find_my_edge.integrations.borkers.common.factory;

import com.example.find_my_edge.integrations.borkers.common.service.BrokerOAuthService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BrokerOAuthFactory {

    private final Map<String, BrokerOAuthService> services;

    public BrokerOAuthFactory(List<BrokerOAuthService> services) {
        this.services =
                services.stream()
                        .collect(Collectors.toMap(
                                BrokerOAuthService::getBrokerName,
                                s -> s
                        ));
    }

    public BrokerOAuthService get(String broker) {
        return services.get(broker);
    }
}