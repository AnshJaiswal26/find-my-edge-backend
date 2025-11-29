package com.example.find_my_edge.dto;


import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class GoogleSheetRequest {
    private String sheetName;
    private String sheetId;
    private List<Objects> data;
}
