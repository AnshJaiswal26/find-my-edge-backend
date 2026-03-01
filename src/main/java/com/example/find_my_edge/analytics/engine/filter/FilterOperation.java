package com.example.find_my_edge.analytics.engine.filter;

import com.example.find_my_edge.analytics.config.FilterConfig;

@FunctionalInterface
public interface FilterOperation {
    boolean apply(Object fieldValue, FilterConfig filter);
}