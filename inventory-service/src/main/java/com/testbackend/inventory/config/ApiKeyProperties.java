package com.testbackend.inventory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "internal")
@Getter @Setter
public class ApiKeyProperties {
    private String apiKey;
}
