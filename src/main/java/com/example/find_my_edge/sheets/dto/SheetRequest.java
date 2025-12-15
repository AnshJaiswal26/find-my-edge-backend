package com.example.find_my_edge.sheets.dto;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SheetRequest {
    private String sheetName;
    private Integer sheetId;
    private Map<Integer, Payload> columnMap;

    @Data
    public static class Payload {
        private String label;
        private Object value;
        private String type;
        private List<String> options;
    }
}
