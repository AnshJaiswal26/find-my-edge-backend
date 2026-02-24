package com.example.find_my_edge.workspace.features;

import java.util.List;

public interface TableService {

    List<String> updateColumnOrder(String page, List<String> order);

    Integer updateColumnWidth(String page, String id, Integer width);
}
