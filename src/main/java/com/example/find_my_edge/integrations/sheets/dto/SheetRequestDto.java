package com.example.find_my_edge.integrations.sheets.dto;

import lombok.Data;

import java.util.List;

@Data
public class SheetRequestDto {
    private String syncId;
    private Boolean autosave;
    private String sheetName;
    private Integer sheetId;
    private Integer rowIndex;
    private List<List<SheetPayload>> payloadsList;
}
