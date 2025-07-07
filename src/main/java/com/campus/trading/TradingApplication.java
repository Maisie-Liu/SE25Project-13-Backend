package com.campus.trading;

import com.campus.trading.config.DeepSeekProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.campus.trading.config.TencentCloudProperties;

/**
 * 校园二手交易平台启动类
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({TencentCloudProperties.class, DeepSeekProperties.class})
public class TradingApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(TradingApplication.class, args);
        String[] beans = ctx.getBeanNamesForType(RequestMappingHandlerMapping.class);
        System.out.println("===== 所有已注册的接口路径 =====");
        for (String bean : beans) {
            RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) ctx.getBean(bean);
            mapping.getHandlerMethods().forEach((k, v) -> System.out.println(k));
        }
        System.out.println("===== 接口路径打印结束 =====");
    }
} 