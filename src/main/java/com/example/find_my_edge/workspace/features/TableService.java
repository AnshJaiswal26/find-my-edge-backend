package com.example.find_my_edge.workspace.features;

public interface TableService {

    void updateColumnWidth(String page, String id, Integer width);

    void updateHighLightedRow(String page, String id, Boolean highlight);
}
