package com.example.find_my_edge.sheets.dto;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GoogleSheetRequest {
    private String sheetName;
    private Integer sheetId;
    Map<String, Object> values;
    Map<String, List<String>> dropdowns;
    Map<String, String> columnMap;
}
