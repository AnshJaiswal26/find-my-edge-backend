package com.example.find_my_edge.workspace.features;

import com.example.find_my_edge.workspace.config.stat.StatConfig;

import java.util.List;
import java.util.Map;

public interface StatService {

    Map<String, Object> getAll(String page);

    StatConfig getById(String page, String id);

    StatConfig create(String page, StatConfig stat);

    StatConfig update(String page, String id, StatConfig stat);

    void delete(String page, String id);

    List<String> updateOrder(String page, List<String> order);

}
