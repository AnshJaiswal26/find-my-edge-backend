package com.example.find_my_edge.integrations.borkers.common.dto;

import com.example.find_my_edge.integrations.borkers.common.enums.ConnectionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ConnectionStatusResponseDto {
    private ConnectionStatus status;
    private Instant lastFetchedAt;
    private String connectedAt;
    private String message;
}
