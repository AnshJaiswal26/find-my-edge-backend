package com.example.find_my_edge.core.workspace.features;

import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;

import java.util.List;
import java.util.Map;

public interface StatService {

    StatDTO addStat(String page, StatDTO stat);

    StatDTO updateStat(String page, String id, StatDTO stat);

    Map<String, Object> getAll(String page);

    void delete(String page, String id);

    List<String> updateStatsOrder(String page, List<String> statsOrder);
}
