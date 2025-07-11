package com.campus.trading.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.qwen3")
public class Qwen3Properties {
    private String apiKey;
    private String baseUrl;
}
