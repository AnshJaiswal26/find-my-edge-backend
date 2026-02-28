package com.example.find_my_edge.analytics.ast.function.enums;

public enum FunctionType {
    PURE,        // ABS, IF, ROUND (no loop)
    AGGREGATE,   // SUM, AVG, WIN_RATE (loop via runner)
    WINDOW       // AVG_N, MAX_N (window loop)
}