package com.ceos.fetch.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry registry;

    // RSS源相关指标
    private Counter rssSourceFetchSuccessCounter;
    private Counter rssSourceFetchFailureCounter;
    private Timer rssSourceFetchTimer;
    private Counter rssSourceQueueSizeCounter;
    private Counter rssSourceQueueErrorCounter;

    // 内容分析相关指标
    private Counter contentAnalysisSuccessCounter;
    private Counter contentAnalysisFailureCounter;
    private Timer contentAnalysisTimer;
    private Counter contentAnalysisRetryCounter;

    // LLM服务相关指标
    private Counter llmApiCallSuccessCounter;
    private Counter llmApiCallFailureCounter;
    private Timer llmApiCallTimer;
    private Counter llmApiCallRetryCounter;

    // 队列相关指标
    private Counter messagePublishedCounter;
    private Counter messageConsumedCounter;
    private Counter messageRejectedCounter;
    private Timer messageProcessingTimer;

    public void recordRssSourceFetchSuccess() {
        if (rssSourceFetchSuccessCounter == null) {
            rssSourceFetchSuccessCounter = Counter.builder("rss.source.fetch.success")
                .description("RSS源抓取成功次数")
                .register(registry);
        }
        rssSourceFetchSuccessCounter.increment();
    }

    public void recordRssSourceFetchFailure() {
        if (rssSourceFetchFailureCounter == null) {
            rssSourceFetchFailureCounter = Counter.builder("rss.source.fetch.failure")
                .description("RSS源抓取失败次数")
                .register(registry);
        }
        rssSourceFetchFailureCounter.increment();
    }

    public Timer.Sample startRssSourceFetchTimer() {
        if (rssSourceFetchTimer == null) {
            rssSourceFetchTimer = Timer.builder("rss.source.fetch.time")
                .description("RSS源抓取耗时")
                .register(registry);
        }
        return Timer.start(registry);
    }

    public void recordRssSourceQueueSize(int size) {
        if (rssSourceQueueSizeCounter == null) {
            rssSourceQueueSizeCounter = Counter.builder("rss.source.queue.size")
                .description("RSS源队列大小")
                .register(registry);
        }
        rssSourceQueueSizeCounter.increment(size);
    }

    public void recordRssSourceQueueError() {
        if (rssSourceQueueErrorCounter == null) {
            rssSourceQueueErrorCounter = Counter.builder("rss.source.queue.error")
                .description("RSS源队列错误次数")
                .register(registry);
        }
        rssSourceQueueErrorCounter.increment();
    }

    public void recordContentAnalysisSuccess() {
        if (contentAnalysisSuccessCounter == null) {
            contentAnalysisSuccessCounter = Counter.builder("content.analysis.success")
                .description("内容分析成功次数")
                .register(registry);
        }
        contentAnalysisSuccessCounter.increment();
    }

    public void recordContentAnalysisFailure() {
        if (contentAnalysisFailureCounter == null) {
            contentAnalysisFailureCounter = Counter.builder("content.analysis.failure")
                .description("内容分析失败次数")
                .register(registry);
        }
        contentAnalysisFailureCounter.increment();
    }

    public Timer.Sample startContentAnalysisTimer() {
        if (contentAnalysisTimer == null) {
            contentAnalysisTimer = Timer.builder("content.analysis.time")
                .description("内容分析耗时")
                .register(registry);
        }
        return Timer.start(registry);
    }

    public void recordContentAnalysisRetry() {
        if (contentAnalysisRetryCounter == null) {
            contentAnalysisRetryCounter = Counter.builder("content.analysis.retry")
                .description("内容分析重试次数")
                .register(registry);
        }
        contentAnalysisRetryCounter.increment();
    }

    public void recordLlmApiCallSuccess() {
        if (llmApiCallSuccessCounter == null) {
            llmApiCallSuccessCounter = Counter.builder("llm.api.call.success")
                .description("LLM API调用成功次数")
                .register(registry);
        }
        llmApiCallSuccessCounter.increment();
    }

    public void recordLlmApiCallFailure() {
        if (llmApiCallFailureCounter == null) {
            llmApiCallFailureCounter = Counter.builder("llm.api.call.failure")
                .description("LLM API调用失败次数")
                .register(registry);
        }
        llmApiCallFailureCounter.increment();
    }

    public Timer.Sample startLlmApiCallTimer() {
        if (llmApiCallTimer == null) {
            llmApiCallTimer = Timer.builder("llm.api.call.time")
                .description("LLM API调用耗时")
                .register(registry);
        }
        return Timer.start(registry);
    }

    public void recordLlmApiCallRetry() {
        if (llmApiCallRetryCounter == null) {
            llmApiCallRetryCounter = Counter.builder("llm.api.call.retry")
                .description("LLM API调用重试次数")
                .register(registry);
        }
        llmApiCallRetryCounter.increment();
    }

    public void recordMessagePublished() {
        if (messagePublishedCounter == null) {
            messagePublishedCounter = Counter.builder("message.published")
                .description("消息发布次数")
                .register(registry);
        }
        messagePublishedCounter.increment();
    }

    public void recordMessageConsumed() {
        if (messageConsumedCounter == null) {
            messageConsumedCounter = Counter.builder("message.consumed")
                .description("消息消费次数")
                .register(registry);
        }
        messageConsumedCounter.increment();
    }

    public void recordMessageRejected() {
        if (messageRejectedCounter == null) {
            messageRejectedCounter = Counter.builder("message.rejected")
                .description("消息拒绝次数")
                .register(registry);
        }
        messageRejectedCounter.increment();
    }

    public Timer.Sample startMessageProcessingTimer() {
        if (messageProcessingTimer == null) {
            messageProcessingTimer = Timer.builder("message.processing.time")
                .description("消息处理耗时")
                .register(registry);
        }
        return Timer.start(registry);
    }

    public MeterRegistry getRegistry() {
        return registry;
    }
} 