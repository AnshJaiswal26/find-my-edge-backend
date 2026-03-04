package com.example.find_my_edge.integrations.borkers.common.dto;

import com.example.find_my_edge.integrations.borkers.common.enums.ConnectionStatus;
import lombok.Data;

@Data
public class ConnectionStatusResponseDto {
    private ConnectionStatus status;
    private String message;

    public ConnectionStatusResponseDto(ConnectionStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
