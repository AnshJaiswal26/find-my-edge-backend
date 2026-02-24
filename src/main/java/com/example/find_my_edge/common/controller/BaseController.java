package com.example.find_my_edge.common.controller;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> buildResponse(T data, String message) {

        boolean isCollection = data instanceof Collection;

        int count = isCollection
                    ? ((Collection<?>) data).size()
                    : (data == null ? 0 : 1);

        boolean empty = data == null ||
                        (isCollection && ((Collection<?>) data).isEmpty());

        Map<String, Object> meta = new HashMap<>();
        meta.put("empty", empty);
        meta.put("count", count);

        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                           .state(ResponseState.SUCCESS)
                           .httpStatus(HttpStatus.OK.value())
                           .message(message)
                           .data(data)
                           .meta(meta)
                           .build()
        );
    }
}