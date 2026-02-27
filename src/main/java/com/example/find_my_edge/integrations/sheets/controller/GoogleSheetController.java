package com.example.find_my_edge.integrations.sheets.controller;

import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.integrations.sheets.dto.SheetRequestDto;
import com.example.find_my_edge.integrations.sheets.service.GoogleSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/sheets")
@RequiredArgsConstructor
public class GoogleSheetController {

    private final GoogleSheetService googleSheetService;

    @GetMapping("/{sheetId}")
    public ResponseEntity<ApiResponse<Object>> getAllSheets(@PathVariable String sheetId) {
        return googleSheetService.getSheetNames(sheetId);
    }

    @PostMapping("/{sheetId}")
    public ResponseEntity<ApiResponse<Object>> insertGoogleSheetData(@PathVariable String sheetId, @RequestBody SheetRequestDto request) {
        return googleSheetService.appendDataToSheet(sheetId, request);
    }

}
