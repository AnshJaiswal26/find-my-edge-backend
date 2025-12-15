package com.example.find_my_edge.sheets.builder;

import com.example.find_my_edge.sheets.dto.SheetRequest;
import com.google.api.services.sheets.v4.model.*;
import com.example.find_my_edge.sheets.utils.SheetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    private Request generateUpdateCellsRequest(int rowIndex, List<CellData> cellData) {

        return new Request().setUpdateCells(new UpdateCellsRequest().setRows(List.of(new RowData().setValues(cellData)))
                                                                    .setFields("userEnteredValue")
                                                                    .setStart(new GridCoordinate().setSheetId(sheetId)
                                                                                                  .setRowIndex(rowIndex)
                                                                                                  .setColumnIndex(0)));
    }

    private ExtendedValue resolveCellValues(SheetRequest.Payload payload, boolean isHeader) {

        if (payload == null) {
            return new ExtendedValue().setStringValue("");
        }

        if (isHeader) {
            return SheetUtil.buildValue(payload.getLabel(), "text");
        }

        return SheetUtil.buildValue(payload.getValue(), payload.getType());
    }

    private Request buildUpdateCellsRequest(int rowIndex, int maxCol, Map<Integer, SheetRequest.Payload> columnMap) {
        List<CellData> cellData = new ArrayList<>();
        boolean isHeader = rowIndex == 0;

        for (int colIndex = 0; colIndex < maxCol; colIndex++) {

            SheetRequest.Payload payload = columnMap.get(colIndex);

            ExtendedValue extendedValue = resolveCellValues(payload, isHeader);
            cellData.add(new CellData().setUserEnteredValue(extendedValue));

            if (payload != null && payload.getType()
                                          .equals("dropdown") && !isHeader) {
                Request request = generateDropdownValidationRequest(rowIndex, colIndex, payload.getOptions());
                requests.add(request);
            }
        }

        return generateUpdateCellsRequest(rowIndex, cellData);
    }

    // ---------- VALUES ----------
    public SheetRequestBuilder setRowValues(
            int rowIndex,
            Map<Integer, SheetRequest.Payload> columnMap
    ) {

        int maxCol = Collections.max(columnMap.keySet()) + 1;

        if (rowIndex == 0) {
            requests.add(buildUpdateCellsRequest(0, maxCol, columnMap));
        }

        int dataRow = rowIndex == 0 ? 1 : rowIndex;

        requests.add(buildUpdateCellsRequest(dataRow, maxCol, columnMap));

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
