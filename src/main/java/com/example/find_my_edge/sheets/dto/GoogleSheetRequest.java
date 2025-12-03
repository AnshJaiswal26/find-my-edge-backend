package com.example.find_my_edge.sheets.dto;


import lombok.Data;

import java.util.List;

@Data
public class GoogleSheetRequest {
    private String sheetName;
    private String sheetId;
    private List<List<Object>> data;
}
