package com.example.find_my_edge.workspace.exception.chart;

public class ChartNotFoundException extends ChartException {
    public ChartNotFoundException(String id) {
        super("Chart not found: " + id);
    }
}