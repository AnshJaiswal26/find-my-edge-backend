package com.example.find_my_edge.integrations.borkers.common.converter;

import com.example.find_my_edge.integrations.borkers.common.enums.Broker;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToBrokerConverter implements Converter<String, Broker> {

    @Override
    public Broker convert(String source) {
        return Broker.valueOf(source.toUpperCase());
    }
}