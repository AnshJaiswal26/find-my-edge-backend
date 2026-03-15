package com.example.find_my_edge.workspace.features;

import com.example.find_my_edge.workspace.config.page.PageGridLayoutConfig;

import java.util.Map;

public interface PageLayoutService {
    Map<String, PageGridLayoutConfig> updateLayout(
            String pageName,
            Map<String, PageGridLayoutConfig> layout
    );
}
