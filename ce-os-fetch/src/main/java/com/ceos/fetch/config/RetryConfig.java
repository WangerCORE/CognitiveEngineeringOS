package com.ceos.fetch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 配置退避策略
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000); // 初始间隔1秒
        backOffPolicy.setMultiplier(2.0); // 每次重试间隔翻倍
        backOffPolicy.setMaxInterval(10000); // 最大间隔10秒
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // 配置重试策略
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(
            3, // 最大重试次数
            Collections.singletonMap(Exception.class, true) // 对所有异常进行重试
        );
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
} 