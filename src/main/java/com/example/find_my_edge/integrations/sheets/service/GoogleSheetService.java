package com.example.find_my_edge.integrations.sheets.service;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.integrations.sheets.exception.SheetFetchException;
import com.example.find_my_edge.integrations.sheets.builder.SheetRequestBuilder;
import com.example.find_my_edge.integrations.sheets.dto.SheetPayload;
import com.example.find_my_edge.integrations.sheets.dto.SheetRequestDto;
import com.example.find_my_edge.integrations.sheets.utils.SheetUtil;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GoogleSheetService {

    private static final Pattern COLUMN_ERROR_PATTERN =
            Pattern.compile("columnIndex\\[(\\d+)] is after last column in grid\\[(\\d+)]");

    private final Sheets sheets;
    private final SheetWriteQueue sheetWriteQueue;
    private final SseEmitterRegistry sseEmitterRegistry;

    public String buildColumnOutOfRangeMessage(String errorMessage) {
        Matcher matcher = COLUMN_ERROR_PATTERN.matcher(errorMessage);

        if (!matcher.find()) {
            return "Invalid column mapping: column index is outside the sheet range.";
        }

        int requestedIndex = Integer.parseInt(matcher.group(1));
        int maxIndex = Integer.parseInt(matcher.group(2)); // grid is count-based

        String requestedCol = SheetUtil.indexToLetter(requestedIndex);
        String maxCol = SheetUtil.indexToLetter(maxIndex);

        return String.format(
                "Invalid column mapping: column \"%s\" does not exist. " +
                "The sheet only contains columns up to \"%s\".",
                requestedCol,
                maxCol
        );
    }


    public int getFirstEmptyRowIndex(String spreadsheetId, String sheetName) throws IOException {
        ValueRange response = sheets.spreadsheets()
                                    .values()
                                    .get(spreadsheetId, sheetName + "!A:A")
                                    .execute();
        return (response.getValues() == null) ? 0 : response.getValues()
                                                            .size();
    }

    private int appendToSheet(String spreadsheetId, SheetRequestDto request) throws IOException {

        String sheetName = request.getSheetName();
        Integer sheetId = request.getSheetId();
        Integer sheetRowIndex = request.getRowIndex();
        List<List<SheetPayload>> payloadsList = request.getPayloadsList();

        int rowIndex = sheetRowIndex != null
                       ? sheetRowIndex
                       : getFirstEmptyRowIndex(spreadsheetId, sheetName);

        List<Request> requests = new SheetRequestBuilder(sheetId)
                .setRowValues(rowIndex, payloadsList)
                .build();

        sheets.spreadsheets()
              .batchUpdate(
                      spreadsheetId,
                      new BatchUpdateSpreadsheetRequest().setRequests(requests)
              )
              .execute();

        return rowIndex;
    }


    private void handleAutosave(String spreadsheetId, SheetRequestDto request) {
        String syncId = request.getSyncId();

        try {
            int rowIndex = appendToSheet(spreadsheetId, request);

            sseEmitterRegistry.send(
                    syncId,
                    ApiResponse.builder()
                               .state(ResponseState.SUCCESS)
                               .httpStatus(HttpStatus.OK.value())
                               .message("Saved to Sheets ✓")
                               .data(Map.of("rowIndex", rowIndex))
                               .build()
            );

        } catch (GoogleJsonResponseException e) {
            sseEmitterRegistry.send(
                    syncId,
                    ApiResponse.builder()
                               .state(ResponseState.ERROR)
                               .httpStatus(HttpStatus.BAD_REQUEST.value())
                               .message(
                                       buildColumnOutOfRangeMessage(
                                               e.getDetails().getMessage()
                                       )
                               )
                               .build()
            );

        } catch (Exception e) {
            sseEmitterRegistry.send(
                    syncId,
                    ApiResponse.builder()
                               .state(ResponseState.ERROR)
                               .httpStatus(HttpStatus.BAD_REQUEST.value())
                               .message(e.getMessage())
                               .build()
            );

        } finally {
            sseEmitterRegistry.complete(syncId);
        }
    }


    private ApiResponse<Object> handleManualSave(String spreadsheetId, SheetRequestDto request) {
        try {
            int rowIndex = appendToSheet(spreadsheetId, request);

            return ApiResponse.builder()
                              .state(ResponseState.SUCCESS)
                              .httpStatus(HttpStatus.OK.value())
                              .message("Saved to Sheets ✓")
                              .data(Map.of("rowIndex", rowIndex))
                              .build();

        } catch (GoogleJsonResponseException e) {
            throw new SheetFetchException(
                    buildColumnOutOfRangeMessage(
                            e.getDetails().getMessage()
                    )
            );
        } catch (IOException e) {
            throw new SheetFetchException(e.getMessage());
        }
    }


    public ResponseEntity<ApiResponse<Object>> appendDataToSheet(String spreadsheetId, SheetRequestDto request) {

        if (Boolean.TRUE.equals(request.getAutosave())) {

            sheetWriteQueue.submit(
                    () -> handleAutosave(spreadsheetId, request)
            );

            return ResponseEntity.ok(
                    ApiResponse.builder()
                               .state(ResponseState.QUEUED)
                               .httpStatus(HttpStatus.OK.value())
                               .message("Sheet request queued")
                               .build()
            );
        }

        ApiResponse<Object> response =
                handleManualSave(spreadsheetId, request);

        return ResponseEntity.ok(response);
    }


    public ResponseEntity<ApiResponse<Object>> getSheetNames(String spreadSheetId) {

        try {
            Spreadsheet spreadsheet = sheets.spreadsheets()
                                            .get(spreadSheetId)
                                            .execute();

            List<SheetDetailsResponse> sheetDetails =
                    spreadsheet.getSheets()
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
                                                .state(ResponseState.SUCCESS)
                                                .httpStatus(HttpStatus.OK.value())
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
