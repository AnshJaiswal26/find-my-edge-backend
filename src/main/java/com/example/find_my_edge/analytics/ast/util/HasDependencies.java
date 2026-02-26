package com.example.find_my_edge.analytics.ast.util;

import java.util.List;

public interface HasDependencies {
    String getId();
    List<String> getDependencies();
}