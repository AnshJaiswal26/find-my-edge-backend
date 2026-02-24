package com.example.find_my_edge.common.exceptions;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.domain.schema.exception.SchemaDependencyException;
import com.example.find_my_edge.domain.schema.exception.SchemaNotFoundException;
import com.example.find_my_edge.trade_import.exception.ImportedTradeNotFoundException;
import com.example.find_my_edge.integrations.sheets.exception.SheetFetchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ImportedTradeNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleTradeNotFound(ImportedTradeNotFoundException ex, HttpServletRequest request) {

        ApiResponse<?> response = ApiResponse.builder()
                                             .state(ResponseState.ERROR)
                                             .httpStatus(HttpStatus.NOT_FOUND.value())
                                             .message(ex.getMessage())
                                             .data(null)
                                             .meta(Map.of(
                                                     "timestamp", LocalDateTime.now()
                                                                               .toString(), "path",
                                                     request.getRequestURI()
                                             ))
                                             .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SheetFetchException.class)
    public ResponseEntity<ApiResponse<?>> handleSheetFetchException(SheetFetchException ex, HttpServletRequest request) {

        ApiResponse<?> response = ApiResponse.builder()
                                             .state(ResponseState.ERROR)
                                             .httpStatus(HttpStatus.NOT_FOUND.value())
                                             .message(ex.getMessage())
                                             .data(null)
                                             .meta(Map.of(
                                                     "timestamp", LocalDateTime.now()
                                                                               .toString(), "path",
                                                     request.getRequestURI()
                                             ))
                                             .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(SchemaNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleSchemaException(SchemaNotFoundException ex, HttpServletRequest request) {

        ApiResponse<?> response = ApiResponse.builder()
                                             .state(ResponseState.ERROR)
                                             .httpStatus(HttpStatus.NOT_FOUND.value())
                                             .message(ex.getMessage())
                                             .data(null)
                                             .meta(Map.of(
                                                     "timestamp", LocalDateTime.now()
                                                                               .toString(), "path",
                                                     request.getRequestURI()
                                             ))
                                             .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SchemaDependencyException.class)
    public ResponseEntity<ApiResponse<?>> handleSchemaDependencyException(
            SchemaDependencyException ex,
            HttpServletRequest request
    ) {

        ApiResponse<?> response = ApiResponse.builder()
                                             .state(ResponseState.ERROR)
                                             .httpStatus(HttpStatus.CONFLICT.value())
                                             .message(ex.getMessage())
                                             .data(null)
                                             .meta(Map.of(
                                                     "timestamp", LocalDateTime.now().toString(),
                                                     "path", request.getRequestURI()
                                             ))
                                             .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
