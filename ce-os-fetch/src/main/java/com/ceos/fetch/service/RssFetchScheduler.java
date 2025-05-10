package com.ceos.fetch.service;

import com.ceos.fetch.entity.RssSource;
import com.ceos.fetch.repository.RssSourceRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RssFetchScheduler {
    private final RssSourceRepository rssSourceRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MetricsService metricsService;

    @Value("${rss.fetch.interval:300000}")
    private long fetchInterval;

    @Value("${spring.application.name}")
    private String applicationName;

    @Scheduled(fixedDelayString = "${rss.fetch.interval:300000}")
    public void scheduleFetch() {
        log.info("开始调度RSS源抓取任务");
        Timer.Sample timer = metricsService.startRssSourceFetchTimer();

        try {
            List<RssSource> activeSources = rssSourceRepository.findByActiveTrue();
            log.info("找到{}个活跃的RSS源", activeSources.size());
            metricsService.recordRssSourceQueueSize(activeSources.size());

            for (RssSource source : activeSources) {
                try {
                    // 发送消息到RabbitMQ队列
                    rabbitTemplate.convertAndSend(
                        "rss.fetch.exchange",
                        "rss.fetch.routing.key",
                        source.getId()
                    );
                    log.info("已将RSS源[{}]加入抓取队列", source.getId());
                    metricsService.recordRssSourceFetchSuccess();
                    metricsService.recordMessagePublished();
                } catch (Exception e) {
                    log.error("将RSS源[{}]加入抓取队列失败", source.getId(), e);
                    metricsService.recordRssSourceFetchFailure();
                    metricsService.recordRssSourceQueueError();
                }
            }
        } catch (Exception e) {
            log.error("调度RSS源抓取任务失败", e);
            metricsService.recordRssSourceFetchFailure();
            metricsService.recordRssSourceQueueError();
        } finally {
            timer.stop(Timer.builder("rss.source.fetch.time")
                .tag("application", applicationName)
                .register(metricsService.getRegistry()));
        }
    }
} 