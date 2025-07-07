package com.campus.trading.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.deepseek")
public class DeepSeekProperties {
    private String apiKey;
    private String baseUrl;
}
