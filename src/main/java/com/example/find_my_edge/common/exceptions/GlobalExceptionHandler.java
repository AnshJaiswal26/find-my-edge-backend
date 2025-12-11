package com.example.find_my_edge.common.exceptions;

import com.example.find_my_edge.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TradeNotFound.class)
    public ResponseEntity<ApiResponse<?>> handleTradeNotFound(TradeNotFound ex, HttpServletRequest request) {

        ApiResponse<?> response = ApiResponse.builder()
                                             .success(false)
                                             .status(HttpStatus.NOT_FOUND.value())
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
                                             .success(false)
                                             .status(HttpStatus.NOT_FOUND.value())
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
}
