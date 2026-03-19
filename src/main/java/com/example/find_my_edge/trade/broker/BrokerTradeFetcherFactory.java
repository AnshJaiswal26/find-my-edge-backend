package com.example.find_my_edge.trade.broker;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BrokerTradeFetcherFactory {

    private final Map<Broker, BrokerTradeFetcher> fetcherMap;

    public BrokerTradeFetcherFactory(List<BrokerTradeFetcher> fetchers) {
        this.fetcherMap = fetchers.stream()
                                  .collect(Collectors.toMap(
                                          BrokerTradeFetcher::getName,
                                          f -> f
                                  ));
    }

    public BrokerTradeFetcher get(Broker broker) {
        return fetcherMap.get(broker);
    }
}
