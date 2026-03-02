package com.example.find_my_edge.integrations.borkers.dhan.dto;

import lombok.Data;

@Data
public class DhanAccessTokenResponseDto {
    private String dhanClientId;
    private String dhanClientName;
    private String dhanClientUcc;
    private String givenPowerOfAttorney;
    private String accessToken;
    private String expiryTime;
}
