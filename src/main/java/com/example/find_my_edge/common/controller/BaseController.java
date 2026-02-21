package com.example.find_my_edge.common.controller;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public abstract class BaseController {

    protected ResponseEntity<ApiResponse<Object>> buildResponse(Object data, String message) {

        boolean isList = data instanceof List;
        int count = isList ? ((List<?>) data).size() : (data == null ? 0 : 1);
        boolean empty = data == null || (isList && ((List<?>) data).isEmpty());

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .state(ResponseState.SUCCESS)
                           .httpStatus(HttpStatus.OK.value())
                           .message(message)
                           .data(data)
                           .meta(Map.of(
                                   "empty", empty,
                                   "count", count
                           ))
                           .build()
        );
    }
}