package com.example.find_my_edge.sheets.service;

import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.common.exceptions.SheetFetchException;
import com.example.find_my_edge.sheets.dto.GoogleSheetRequest;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleSheetService {

    private final Sheets sheets;

    // -- Helper functions --
    public String indexToLetter(int index) {
        index++; // convert 0-based → 1-based
        StringBuilder col = new StringBuilder();

        while (index > 0) {
            int rem = (index - 1) % 26;
            col.append((char) ('A' + rem));
            index = (index - 1) / 26;
        }

        return col.reverse()
                  .toString();
    }


    public int letterToIndex(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1; // zero-based
    }


    public Sheet getSheetByName(String spreadSheetId, String sheetName) throws IOException {

        Spreadsheet spreadsheet = sheets.spreadsheets()
                                        .get(spreadSheetId)
                                        .execute();
        List<Sheet> sheetList = spreadsheet.getSheets()
                                           .stream()
                                           .filter(s -> s.getProperties()
                                                         .getTitle()
                                                         .equals(sheetName))
                                           .toList();

        if (sheetList.isEmpty()) {
            throw new SheetFetchException("Sheet not found: " + sheetName);
        }

        return sheetList.getFirst();
    }

    public int findFirstEmptyRow(String spreadsheetId, String sheetName) throws IOException {
        ValueRange response = sheets.spreadsheets()
                                    .values()
                                    .get(spreadsheetId, sheetName + "!A:A")
                                    .execute();
        return (response.getValues() == null) ? 0 : response.getValues()
                                                            .size();
    }

    public Request insertNewEmptyRow(int rowIndex, int sheetId) {
        return new Request().setInsertDimension(new InsertDimensionRequest().setRange(
                                                                                    new DimensionRange().setSheetId(sheetId)
                                                                                                        .setDimension("ROWS")
                                                                                                        .setStartIndex(rowIndex)
                                                                                                        .setEndIndex(rowIndex + 1))
                                                                            .setInheritFromBefore(true));
    }

//    public Request setCellValues(int rowIndex, int columnCount, int sheetId, Map<String, Integer> columnMap, Map<String, Object> values) {
//        List<CellData> cellData = new ArrayList<>();
//
//        int maxCol = Collections.max(columnMap.values()) + 1;
//
//        if (maxCol > columnCount) throw new SheetFetchException(
//                "Error: attempting to write column " + indexToLetter(maxCol - 1) + " beyond max " + indexToLetter(
//                        columnCount - 1));
//
//        for (int col = 0; col < maxCol; col++) {
//            Object val = "";
//            for (var entry : values.entrySet()) {
//                if (columnMap.get(entry.getKey()) == col) {
//                    val = entry.getValue();
//                }
//            }
//            cellData.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(val.toString())));
//        }
//
//        return new Request().setUpdateCells(new UpdateCellsRequest().setRows(List.of(new RowData().setValues(cellData)))
//                                                                    .setFields("userEnteredValue")
//                                                                    .setStart(new GridCoordinate().setSheetId(sheetId)
//                                                                                                  .setRowIndex(rowIndex)
//                                                                                                  .setColumnIndex(0)));
//    }

    public Request setCellValues(int rowIndex, int columnCount, int sheetId, Map<String, Integer> columnMap, Map<String, Object> values) {

        List<CellData> cellData = new ArrayList<>();

        int maxCol = Collections.max(columnMap.values()) + 1;

        if (maxCol > columnCount) {
            throw new SheetFetchException(
                    "Error: attempting to write column " + indexToLetter(maxCol - 1)
                    + " beyond max " + indexToLetter(columnCount - 1));
        }

        for (int col = 0; col < maxCol; col++) {

            // Let null mean "no update"
            Object val = null;

            for (var entry : values.entrySet()) {
                if (columnMap.get(entry.getKey()) == col) {
                    val = entry.getValue();
                }
            }

            ExtendedValue ev = new ExtendedValue();

            if (val == null || val.toString()
                                  .isBlank()) {
                // keep empty — formatting stays intact
                ev.setStringValue(null);
            } else {
                // Send user-entered string
                // Google Sheets will interpret it according to the cell format
                ev.setStringValue(val.toString());
            }

            cellData.add(new CellData().setUserEnteredValue(ev));
        }

        return new Request().setUpdateCells(
                new UpdateCellsRequest()
                        .setRows(List.of(new RowData().setValues(cellData)))
                        .setFields("userEnteredValue")  // only update value, not format
                        .setStart(
                                new GridCoordinate()
                                        .setSheetId(sheetId)
                                        .setRowIndex(rowIndex)
                                        .setColumnIndex(0)
                        )
        );
    }


    public List<Request> setCellDropdowns(int rowIndex, int sheetId, Map<String, List<String>> dropdowns, Map<String, Integer> columnMap) {

        List<Request> dropdownReqList = new ArrayList<>();

        for (var entry : dropdowns.entrySet()) {
            String field = entry.getKey();
            List<String> opts = entry.getValue();
            int col = columnMap.get(field);

            DataValidationRule rule = new DataValidationRule().setCondition(
                                                                      new BooleanCondition().setType("ONE_OF_LIST")
                                                                                            .setValues(opts.stream()
                                                                                                           .map(o -> new ConditionValue().setUserEnteredValue(o))
                                                                                                           .toList()))
                                                              .setShowCustomUi(true);

            GridRange range = new GridRange().setSheetId(sheetId)
                                             .setStartRowIndex(rowIndex)
                                             .setEndRowIndex(rowIndex + 1)
                                             .setStartColumnIndex(col)
                                             .setEndColumnIndex(col + 1);

            dropdownReqList.add(new Request().setSetDataValidation(new SetDataValidationRequest().setRange(range)
                                                                                                 .setRule(rule)));
        }

        return dropdownReqList;
    }


    // -- service functions --
    public ResponseEntity<ApiResponse<Object>> appendDataToSheet(String spreadsheetId, GoogleSheetRequest request) {
        try {
            String sheetName = request.getSheetName();
            Map<String, Object> values = request.getValues();
            Map<String, List<String>> dropdowns = request.getDropdowns();
            Map<String, Integer> columnMap = request.getColumnMap();

            Sheet sheet = getSheetByName(spreadsheetId, sheetName);
            int columnCount = sheet.getProperties()
                                   .getGridProperties()
                                   .getColumnCount();

            int sheetId = sheet.getProperties()
                               .getSheetId();

            int rowIndex = findFirstEmptyRow(spreadsheetId, sheetName);

            List<Request> requests = new ArrayList<>();

            Request emptyRowReq = insertNewEmptyRow(rowIndex, sheetId);
            requests.add(emptyRowReq);

            Request cellValueReq = setCellValues(rowIndex, columnCount, sheetId, columnMap, values);
            requests.add(cellValueReq);


            List<Request> cellDropdownReq = setCellDropdowns(rowIndex, sheetId, dropdowns, columnMap);
            requests.addAll(cellDropdownReq);


            // EXECUTE ALL IN ONE CALL
            sheets.spreadsheets()
                  .batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests))
                  .execute();

            return ResponseEntity.ok(ApiResponse.builder()
                                                .success(true)
                                                .status(HttpStatus.OK.value())
                                                .message("Row inserted successfully")
                                                .build());

        } catch (Exception e) {
            throw new SheetFetchException(e.getMessage());
        }
    }


    public ResponseEntity<ApiResponse<Object>> getSheetNames(String spreadSheetId) {

        try {
            Spreadsheet spreadsheet = sheets.spreadsheets()
                                            .get(spreadSheetId)
                                            .execute();

            List<String> sheetNames = spreadsheet.getSheets()
                                                 .stream()
                                                 .map(s -> s.getProperties()
                                                            .getTitle())
                                                 .toList();


            return ResponseEntity.ok(ApiResponse.builder()
                                                .success(true)
                                                .status(HttpStatus.OK.value())
                                                .message("Sheet connected")
                                                .data(sheetNames)
                                                .meta(Map.of("empty", sheetNames.isEmpty(), "count", sheetNames.size()))
                                                .build());
        } catch (IOException e) {
            throw new SheetFetchException("Invalid Sheet ID or the sheet is not accessible.");
        }
    }


}
