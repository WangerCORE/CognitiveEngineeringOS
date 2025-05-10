package com.ceos.formatter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMConfig {
    private String apiKey;
    private String apiEndpoint;
    private String model;
    private Integer maxTokens = 2000;
    private Double temperature = 0.7;
    private Integer timeout = 30;
} 