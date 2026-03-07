package com.example.find_my_edge.analytics.service;

import com.example.find_my_edge.analytics.config.GroupConfig;

public interface ComputeGroupAggregate {

    Object compute(GroupConfig groupConfig);
}
