package com.example.find_my_edge.core.schema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayDto {
    private String format;   // NUMBER, CURRENCY, RATIO, HH:mm:ss
    private Integer decimals;
}
