package com.example.find_my_edge.controller;

import com.example.find_my_edge.dto.GoogleSheetRequest;
import com.example.find_my_edge.service.GoogleSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sheets")
@RequiredArgsConstructor
public class GoogleSheetController {

    private final GoogleSheetService googleSheetService;

    @GetMapping
    public ResponseEntity<List<String>> getAllSheets(@RequestParam String sheetId) {
        return googleSheetService.getSheetNames(sheetId);
    }

    @PostMapping("/append")
    public ResponseEntity<String> insertGoogleSheetData(@RequestBody GoogleSheetRequest request) {
        return googleSheetService.appendDataToSheet(request);
    }

}
