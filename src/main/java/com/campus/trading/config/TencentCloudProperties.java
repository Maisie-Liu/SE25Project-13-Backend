package com.campus.trading.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.tencentcloud")
public class TencentCloudProperties {
    private String secretId;
    private String secretKey;
    private String region;
}
