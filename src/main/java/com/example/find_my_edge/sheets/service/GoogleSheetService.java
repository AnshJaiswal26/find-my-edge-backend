package com.example.find_my_edge.sheets.service;

import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.common.exceptions.SheetFetchException;
import com.example.find_my_edge.sheets.builder.SheetRequestBuilder;
import com.example.find_my_edge.sheets.dto.SheetRequest;
import com.example.find_my_edge.sheets.utils.SheetUtil;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleSheetService {

    private final Sheets sheets;

    public int getFirstEmptyRowIndex(String spreadsheetId, String sheetName) throws IOException {
        ValueRange response = sheets.spreadsheets()
                                    .values()
                                    .get(spreadsheetId, sheetName + "!A:A")
                                    .execute();
        return (response.getValues() == null) ? 0 : response.getValues()
                                                            .size();
    }

    public ResponseEntity<ApiResponse<Object>> appendDataToSheet(String spreadsheetId, SheetRequest request) {
        String sheetName = request.getSheetName();
        int sheetId = request.getSheetId();
        Map<Integer, SheetRequest.Payload> columnMap = request.getColumnMap();

        int maxCol = Collections.max(columnMap.keySet()) + 1;

        try {
            int rowIndex = getFirstEmptyRowIndex(spreadsheetId, sheetName);

            List<Request> requests = new SheetRequestBuilder(sheetId).setRowValues(rowIndex, columnMap)
                                                                     .build();

            // EXECUTE ALL IN ONE CALL
            sheets.spreadsheets()
                  .batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests))
                  .execute();

            return ResponseEntity.ok(ApiResponse.builder()
                                                .success(true)
                                                .status(HttpStatus.OK.value())
                                                .message("Row inserted successfully")
                                                .build());
        } catch (GoogleJsonResponseException e) {
            throw new SheetFetchException(
                    "Column '" + SheetUtil.indexToLetter(maxCol - 1) + "' does not exist in the '" + sheetName + "'");
        } catch (IOException e) {
            throw new SheetFetchException(e.getMessage());
        }
    }


    public ResponseEntity<ApiResponse<Object>> getSheetNames(String spreadSheetId) {

        try {
            Spreadsheet spreadsheet = sheets.spreadsheets()
                                            .get(spreadSheetId)
                                            .execute();

            List<SheetDetailsResponse> sheetDetails = spreadsheet.getSheets()
                                                                 .stream()
                                                                 .map(s -> {
                                                                     SheetProperties properties = s.getProperties();
                                                                     return new SheetDetailsResponse(
                                                                             properties.getTitle(),
                                                                             properties.getSheetId()
                                                                     );
                                                                 })
                                                                 .toList();

            return ResponseEntity.ok(ApiResponse.builder()
                                                .success(true)
                                                .status(HttpStatus.OK.value())
                                                .message("Google sheets connected successfully")
                                                .data(sheetDetails)
                                                .meta(Map.of(
                                                        "empty", sheetDetails.isEmpty(),
                                                        "count", sheetDetails.size()
                                                ))
                                                .build());
        } catch (IOException e) {
            throw new SheetFetchException("Invalid Sheet ID or the sheet is not accessible.");
        }
    }


}
