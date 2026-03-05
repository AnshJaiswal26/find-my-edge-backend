package com.example.find_my_edge.trade.broker;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BrokerTradeFetcherFactory {

    private final Map<String, BrokerTradeFetcher> fetcherMap;

    public BrokerTradeFetcherFactory(List<BrokerTradeFetcher> fetchers) {
        this.fetcherMap = fetchers.stream()
                                  .collect(Collectors.toMap(
                                          BrokerTradeFetcher::getName,
                                          f -> f
                                  ));
    }

    public BrokerTradeFetcher get(String name) {
        return fetcherMap.get(name);
    }
}
