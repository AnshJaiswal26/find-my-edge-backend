package com.example.find_my_edge.common.response;

import com.example.find_my_edge.common.enums.ResponseState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int httpStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ResponseState state;

    private String message;
    private T data;
    private Map<String, Object> meta;
}
