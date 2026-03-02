package com.example.find_my_edge.integrations.borkers.dhan.dto;

import lombok.Data;
import java.util.List;

@Data
public class DhanTradeResponseDto {
    private List<DhanTradeDto> data;
}