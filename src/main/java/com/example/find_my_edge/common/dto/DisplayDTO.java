package com.example.find_my_edge.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayDTO {
    private String format;   // NUMBER, CURRENCY, RATIO, HH:mm:ss
    private Integer decimals;
}
