package com.example.find_my_edge.integrations.sheets.utils;

import com.google.api.services.sheets.v4.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiFunction;

public final class SheetUtil {

    private SheetUtil() {
        throw new AssertionError("Utility Class");
    }

    public static String indexToLetter(int index) {
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


    public static int letterToIndex(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1; // zero-based
    }

    public static DataValidationRule dropdownRule(List<String> options) {
        return new DataValidationRule().setCondition(
                                               new BooleanCondition().setType("ONE_OF_LIST")
                                                                     .setValues(options.stream()
                                                                                       .map(o -> new ConditionValue().setUserEnteredValue(o))
                                                                                       .toList()))
                                       .setShowCustomUi(true);
    }

    public static GridRange singleCellRange(int row, int col) {
        return new GridRange().setStartRowIndex(row)
                              .setEndRowIndex(row + 1)
                              .setStartColumnIndex(col)
                              .setEndColumnIndex(col + 1);
    }

    public static CellData buildCell(Object value, String type) {
        CellData cell = new CellData();

        BiFunction<String, String, CellFormat> biFunction = (format, pattern) -> new CellFormat().setNumberFormat(
                new NumberFormat().setType(format).setPattern(pattern)
        );

        return switch (type) {
            case "number" -> {
                cell.setUserEnteredValue(
                        new ExtendedValue().setNumberValue(Double.parseDouble(value.toString()))
                );
                yield cell;
            }

            case "time" -> {
                LocalTime time = LocalTime.parse(value.toString());
                double fractionOfDay = (time.toSecondOfDay()) / 86400.0;

                cell.setUserEnteredValue(
                        new ExtendedValue().setNumberValue(fractionOfDay)
                ).setUserEnteredFormat(biFunction.apply("TIME", "hh:mm:ss AM/PM"));

                yield cell;
            }

            case "formula" -> cell.setUserEnteredValue(
                    new ExtendedValue().setFormulaValue(value.toString())
            );

            case "date" -> {
                LocalDate date = LocalDate.parse(value.toString());
                LocalDate base = LocalDate.of(1899, 12, 30);
                double between = ChronoUnit.DAYS.between(base, date);

                cell.setUserEnteredValue(
                        new ExtendedValue().setNumberValue(between)
                ).setUserEnteredFormat(biFunction.apply("DATE", "dd-MMM-yy"));

                yield cell;
            }

            default -> cell.setUserEnteredValue(
                    new ExtendedValue().setStringValue(value.toString())
            );
        };

    }

}