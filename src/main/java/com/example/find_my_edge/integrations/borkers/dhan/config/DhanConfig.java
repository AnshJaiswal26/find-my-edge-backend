package com.example.find_my_edge.integrations.borkers.dhan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dhan")
@Data
public class DhanConfig {
    private String baseUrl;
    private String authUrl;
    private String clientId;
    private String appId;
    private String appSecret;
}
