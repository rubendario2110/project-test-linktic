package com.testbackend.inventory.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor apiKeyInterceptor(ApiKeyProperties props) {
        return template -> template.header("X-INTERNAL-API-KEY", props.getApiKey());
    }
}
