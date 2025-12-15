package com.example.find_my_edge.sheets.utils;

import com.example.find_my_edge.sheets.dto.SheetRequest;
import com.google.api.services.sheets.v4.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class SheetUtil {

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
        return new DataValidationRule().setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                                                                           .setValues(options.stream()
                                                                                             .map(o -> new ConditionValue().setUserEnteredValue(
                                                                                                     o))
                                                                                             .toList()))
                                       .setShowCustomUi(true);
    }

    public static GridRange singleCellRange(int row, int col) {
        return new GridRange().setStartRowIndex(row)
                              .setEndRowIndex(row + 1)
                              .setStartColumnIndex(col)
                              .setEndColumnIndex(col + 1);
    }

    public static ExtendedValue buildValue(Object value, String type) {

        return switch (type) {

            case "number" -> new ExtendedValue().setNumberValue(Double.parseDouble(value.toString()));

            case "time" -> {
                LocalTime time = LocalTime.parse(value.toString());
                double fractionOfDay = (time.toSecondOfDay()) / 86400.0;
                yield new ExtendedValue().setNumberValue(fractionOfDay);
            }

            case "formula" -> new ExtendedValue().setFormulaValue(value.toString());

            case "date" -> {
                LocalDate date = LocalDate.parse(value.toString());
                LocalDate base = LocalDate.of(1899, 12, 30);
                double between = ChronoUnit.DAYS.between(base, date);
                yield new ExtendedValue().setNumberValue(between);
            }

            default -> new ExtendedValue().setStringValue(value.toString());
        };

    }

}