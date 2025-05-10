package com.ceos.fetch.service;

import com.ceos.fetch.entity.RssEntry;
import com.ceos.fetch.entity.RssSource;
import com.ceos.fetch.repository.RssEntryRepository;
import com.ceos.fetch.repository.RssSourceRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RssFetchService {
    private final RssSourceRepository sourceRepository;
    private final RssEntryRepository entryRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RetryService retryService;
    private final MetricsService metricsService;

    @Value("${rss.fetch.interval}")
    private long fetchInterval;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @Scheduled(fixedDelayString = "${rss.fetch.interval}")
    @Transactional
    public void fetchAllActiveSources() {
        log.info("开始抓取所有活跃的RSS源");
        List<RssSource> activeSources = sourceRepository.findByActiveTrue();
        
        for (RssSource source : activeSources) {
            Timer.Sample timer = metricsService.startRssSourceFetchTimer(source.getId());
            try {
                fetchSource(source);
                metricsService.recordRssSourceFetchSuccess(source.getId());
            } catch (Exception e) {
                log.error("抓取RSS源失败: {}", source.getUrl(), e);
                source.setLastFetchError(e.getMessage());
                sourceRepository.save(source);
                metricsService.recordRssSourceFetchFailure(source.getId());
            } finally {
                metricsService.stopRssSourceFetchTimer(timer, source.getId());
            }
        }
    }

    private void fetchSource(RssSource source) {
        log.info("开始抓取RSS源: {}", source.getUrl());
        
        retryService.executeWithRetry("fetch-rss-source", () -> {
            URL feedUrl = new URL(source.getUrl());
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            
            for (SyndEntry entry : feed.getEntries()) {
                if (!entryRepository.existsByGuid(entry.getUri())) {
                    Timer.Sample timer = metricsService.startRssEntryProcessTimer(source.getId());
                    try {
                        RssEntry rssEntry = new RssEntry();
                        rssEntry.setSource(source);
                        rssEntry.setTitle(entry.getTitle());
                        rssEntry.setDescription(entry.getDescription() != null ? entry.getDescription().getValue() : null);
                        rssEntry.setLink(entry.getLink());
                        rssEntry.setAuthor(entry.getAuthor());
                        rssEntry.setPublishedDate(entry.getPublishedDate() != null ? 
                            entry.getPublishedDate().toInstant() : null);
                        rssEntry.setGuid(entry.getUri());
                        rssEntry.setProcessed(false);
                        
                        entryRepository.save(rssEntry);
                        
                        // 发送消息到RabbitMQ
                        rabbitTemplate.convertAndSend(routingKey, rssEntry.getId());
                        
                        metricsService.recordRssEntryProcessSuccess(source.getId());
                    } catch (Exception e) {
                        log.error("处理RSS条目失败: {}", entry.getUri(), e);
                        metricsService.recordRssEntryProcessFailure(source.getId());
                        throw e;
                    } finally {
                        metricsService.stopRssEntryProcessTimer(timer, source.getId());
                    }
                }
            }
            
            source.setLastFetchTime(LocalDateTime.now());
            source.setLastFetchError(null);
            sourceRepository.save(source);
            
            return null;
        });
    }
} 