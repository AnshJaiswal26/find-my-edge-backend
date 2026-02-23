package com.example.find_my_edge.core.workspace.features;

import java.util.List;

public interface TableService {

    List<String> updateColumnOrder(Long workspaceId, String page, List<String> order);

    Integer updateColumnWidth(Long workspaceId, String page, String id, Integer width);
}
