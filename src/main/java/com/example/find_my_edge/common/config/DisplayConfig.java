package com.example.find_my_edge.common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayConfig {
    private String format;   // NUMBER, CURRENCY, RATIO, HH:mm:ss
    private Integer decimals;
}
