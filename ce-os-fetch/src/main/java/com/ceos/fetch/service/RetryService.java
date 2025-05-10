package com.ceos.fetch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {
    private final RetryTemplate retryTemplate;

    public <T> T executeWithRetry(String operationName, RetryCallback<T> callback) {
        try {
            return retryTemplate.execute(context -> {
                int retryCount = context.getRetryCount();
                if (retryCount > 0) {
                    log.warn("重试操作 [{}] - 第{}次重试", operationName, retryCount);
                }
                return callback.execute();
            });
        } catch (Exception e) {
            log.error("操作 [{}] 在重试后仍然失败", operationName, e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface RetryCallback<T> {
        T execute() throws Exception;
    }
} 