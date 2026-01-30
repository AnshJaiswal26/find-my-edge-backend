package com.example.find_my_edge.sheets.builder;

import com.example.find_my_edge.sheets.dto.SheetPayload;
import com.google.api.services.sheets.v4.model.*;
import com.example.find_my_edge.sheets.utils.SheetUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SheetRequestBuilder {

    private final int sheetId;
    private final List<Request> requests = new ArrayList<>();

    public SheetRequestBuilder(int sheetId) {
        this.sheetId = sheetId;
    }

    private Request generateDropdownValidationRequest(int rowIndex, int colIndex, List<String> options) {
        GridRange gridRange = SheetUtil.singleCellRange(rowIndex, colIndex)
                                       .setSheetId(sheetId);
        DataValidationRule dataValidationRule = SheetUtil.dropdownRule(options);

        return new Request().setSetDataValidation(
                new SetDataValidationRequest().setRange(gridRange)
                                              .setRule(dataValidationRule));
    }

    private Request generateSingleCellUpdateRequest(int rowIndex, int colIndex, CellData cellData, String type) {

        if (rowIndex == 0) {
            cellData.setUserEnteredFormat(
                    new CellFormat().setTextFormat(
                            new TextFormat().setBold(true)
                    )
            );
        }

        String field = switch (type) {
            case "number", "time", "date" -> "userEnteredValue,userEnteredFormat.numberFormat";
            default -> "userEnteredValue";
        };


        return new Request().setUpdateCells(
                new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                          .setSheetId(sheetId)
                                          .setRowIndex(rowIndex)
                                          .setColumnIndex(colIndex))
                        .setRows(List.of(new RowData().setValues(List.of(cellData))))
                        .setFields(rowIndex == 0
                                   ? "userEnteredValue,userEnteredFormat.textFormat.bold"
                                   : field)
        );
    }

    private void buildUpdateCellsRequest(int rowIndex, List<SheetPayload> payloads) {
        boolean isHeader = rowIndex == 0;

        for (SheetPayload payload : payloads) {
            int colIndex = payload.getMappedColumn();
            String type = payload.getType();

            CellData cellData = isHeader
                                ? new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(payload.getLabel()))
                                : SheetUtil.buildCell(payload.getValue(), type);

            Request valueRequest = generateSingleCellUpdateRequest(rowIndex, colIndex, cellData, type);
            requests.add(valueRequest);

            if (payload.getType()
                       .equals("dropdown") && !isHeader) {
                Request dropdownRequest = generateDropdownValidationRequest(rowIndex, colIndex, payload.getOptions());
                requests.add(dropdownRequest);
            }
        }

    }

    // ---------- VALUES ----------
    public SheetRequestBuilder setRowValues(int rowIndex, List<List<SheetPayload>> payloadsList) {

        for (var payloads : payloadsList) {

            if (rowIndex == 0) {
                buildUpdateCellsRequest(0, payloads);
            }

            int dataRow = rowIndex == 0 ? 1 : rowIndex;

            buildUpdateCellsRequest(dataRow, payloads);

            rowIndex++;
        }

        return this;
    }

    public SheetRequestBuilder setEmptyRow(int rowIndex) {
        requests.add(new Request().setInsertDimension(
                new InsertDimensionRequest()
                        .setRange(new DimensionRange().setSheetId(sheetId)
                                                      .setDimension("ROWS")
                                                      .setStartIndex(rowIndex)
                                                      .setEndIndex(rowIndex + 1))));
        return this;
    }

    // ---------- FINAL ----------
    public List<Request> build() {
        return requests;
    }
}
