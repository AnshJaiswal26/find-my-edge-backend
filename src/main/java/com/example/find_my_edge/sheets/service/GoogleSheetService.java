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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleSheetService {

    private final Sheets sheets;

    public String indexToColumnLetter(int index) {
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


    public int columnLetterToIndex(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1; // zero-based
    }


    public Sheet getSheetByName(String spreadSheetId, String sheetName) throws IOException, SheetFetchException {

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


    public ResponseEntity<ApiResponse<?>> appendDataToSheet(String spreadsheetId, GoogleSheetRequest request) {
        try {
            String sheetName = request.getSheetName();
            Map<String, Object> values = request.getValues();
            Map<String, List<String>> dropdowns = request.getDropdowns();
            Map<String, String> columnMap = request.getColumnMap();

            Sheet sheet = getSheetByName(spreadsheetId, sheetName);
            int columnCount = sheet.getProperties()
                                   .getGridProperties()
                                   .getColumnCount();
            int sheetId = sheet.getProperties()
                               .getSheetId();


            // FIND FIRST EMPTY ROW
            ValueRange response = sheets.spreadsheets()
                                        .values()
                                        .get(spreadsheetId, sheetName + "!A:A")
                                        .execute();


            int rowIndex = (response.getValues() == null) ? 0 : response.getValues()
                                                                        .size();

            List<Request> requests = new ArrayList<>();

            // INSERT A NEW EMPTY ROW
            requests.add(new Request().setInsertDimension(new InsertDimensionRequest().setRange(
                    new DimensionRange().setSheetId(sheetId)
                                        .setDimension("ROWS")
                                        .setStartIndex(rowIndex)
                                        .setEndIndex(rowIndex + 1))));

            // SET CELL VALUES IN THAT ROW
            List<CellData> cellData = new ArrayList<>();

            int maxCol = Collections.max(columnMap.values()
                                                  .stream()
                                                  .map(this::columnLetterToIndex)
                                                  .toList()) + 1;

            if (maxCol > columnCount) throw new SheetFetchException(
                    "Error: attempting to write column " + indexToColumnLetter(
                            maxCol - 1) + " beyond max " + indexToColumnLetter(columnCount - 1));

            for (int col = 0; col < maxCol; col++) {
                Object val = "";
                for (var entry : values.entrySet()) {
                    if (columnLetterToIndex(columnMap.get(entry.getKey())) == col) {
                        val = entry.getValue();
                    }
                }
                cellData.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(val.toString())));
            }

            requests.add(new Request().setUpdateCells(
                    new UpdateCellsRequest().setRows(List.of(new RowData().setValues(cellData)))
                                            .setFields("userEnteredValue")
                                            .setStart(new GridCoordinate().setSheetId(sheetId)
                                                                          .setRowIndex(rowIndex)
                                                                          .setColumnIndex(0))));

            // ADD DROPDOWNS ONLY TO THIS ROW
            for (var entry : dropdowns.entrySet()) {
                String field = entry.getKey();
                List<String> opts = entry.getValue();
                int col = columnLetterToIndex(columnMap.get(field));

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

                requests.add(new Request().setSetDataValidation(new SetDataValidationRequest().setRange(range)
                                                                                              .setRule(rule)));
            }

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


    public ResponseEntity<ApiResponse<?>> getSheetNames(String spreadSheetId) {

        try {
            Spreadsheet spreadsheet = sheets.spreadsheets()
                                            .get(spreadSheetId)
                                            .execute();

            Map<String, Map<String, Integer>> sheetNames = spreadsheet.getSheets()
                                                                      .stream()
                                                                      .map(Sheet::getProperties)
                                                                      .collect(Collectors.toMap(
                                                                              SheetProperties::getTitle, p -> Map.of(
                                                                                      "sheetId", p.getSheetId(),
                                                                                      "columnCount",
                                                                                      p.getGridProperties()
                                                                                       .getColumnCount()
                                                                              )
                                                                      ));


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
