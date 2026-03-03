package com.example.find_my_edge.integrations.borkers.dhan.dto;

import com.example.find_my_edge.common.enums.ResponseState;
import lombok.Data;

@Data
public class ConnectionStatusResponseDto {
    private ResponseState code;
    private String message;
}
