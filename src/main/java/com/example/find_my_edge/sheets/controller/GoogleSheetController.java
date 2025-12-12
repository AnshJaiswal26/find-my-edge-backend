package com.example.find_my_edge.sheets.controller;

import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.sheets.dto.GoogleSheetRequest;
import com.example.find_my_edge.sheets.service.GoogleSheetService;
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
    public ResponseEntity<ApiResponse<Object>> insertGoogleSheetData(@PathVariable String sheetId, @RequestBody GoogleSheetRequest request) {
        return googleSheetService.appendDataToSheet(sheetId, request);
    }

}
