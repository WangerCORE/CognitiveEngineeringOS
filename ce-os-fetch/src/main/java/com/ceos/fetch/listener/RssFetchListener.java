package com.ceos.fetch.listener;

import com.ceos.fetch.service.RssFetchService;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssFetchListener {
    private final RssFetchService rssFetchService;
    private final MetricsService metricsService;

    @RabbitListener(queues = "${rss.fetch.queue:rss.fetch.queue}")
    public void handleFetchMessage(String sourceId) {
        log.info("收到RSS源抓取请求，sourceId: {}", sourceId);
        Timer.Sample timer = metricsService.startMessageProcessingTimer();
        metricsService.recordMessageConsumed();

        try {
            rssFetchService.fetchSource(sourceId);
            log.info("RSS源抓取完成，sourceId: {}", sourceId);
            metricsService.recordRssSourceFetchSuccess();
        } catch (Exception e) {
            log.error("RSS源抓取失败，sourceId: {}", sourceId, e);
            metricsService.recordRssSourceFetchFailure();
            metricsService.recordMessageRejected();
            throw e; // 重新抛出异常，触发消息重试
        } finally {
            timer.stop(Timer.builder("message.processing.time")
                .tag("sourceId", sourceId)
                .register(metricsService.getRegistry()));
        }
    }
} 