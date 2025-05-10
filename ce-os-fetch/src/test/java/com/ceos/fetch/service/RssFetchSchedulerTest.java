package com.ceos.fetch.service;

import com.ceos.fetch.entity.RssSource;
import com.ceos.fetch.repository.RssSourceRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RssFetchSchedulerTest {

    @Mock
    private RssSourceRepository rssSourceRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MetricsService metricsService;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private RssFetchScheduler rssFetchScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rssFetchScheduler, "applicationName", "test-app");
    }

    @Test
    void scheduleFetch_ShouldSendMessagesForActiveSources() {
        // 准备测试数据
        RssSource source1 = new RssSource();
        source1.setId("source-1");
        source1.setActive(true);

        RssSource source2 = new RssSource();
        source2.setId("source-2");
        source2.setActive(true);

        List<RssSource> activeSources = Arrays.asList(source1, source2);

        // 模拟依赖行为
        when(rssSourceRepository.findByActiveTrue()).thenReturn(activeSources);
        when(metricsService.startRssSourceFetchTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(metricsService.getRegistry()).thenReturn(meterRegistry);

        // 执行测试
        rssFetchScheduler.scheduleFetch();

        // 验证结果
        verify(rssSourceRepository).findByActiveTrue();
        verify(rabbitTemplate, times(2)).convertAndSend(
            eq("rss.fetch.exchange"),
            eq("rss.fetch.routing.key"),
            anyString()
        );
        verify(metricsService, times(2)).recordRssSourceFetchSuccess();
    }

    @Test
    void scheduleFetch_WhenRepositoryThrowsException_ShouldHandleError() {
        // 模拟依赖行为
        when(rssSourceRepository.findByActiveTrue()).thenThrow(new RuntimeException("数据库错误"));
        when(metricsService.startRssSourceFetchTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(metricsService.getRegistry()).thenReturn(meterRegistry);

        // 执行测试
        rssFetchScheduler.scheduleFetch();

        // 验证结果
        verify(rssSourceRepository).findByActiveTrue();
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(metricsService).recordRssSourceFetchFailure();
    }

    @Test
    void scheduleFetch_WhenRabbitTemplateThrowsException_ShouldHandleError() {
        // 准备测试数据
        RssSource source = new RssSource();
        source.setId("source-1");
        source.setActive(true);

        // 模拟依赖行为
        when(rssSourceRepository.findByActiveTrue()).thenReturn(List.of(source));
        when(metricsService.startRssSourceFetchTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(metricsService.getRegistry()).thenReturn(meterRegistry);
        doThrow(new RuntimeException("RabbitMQ错误"))
            .when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        // 执行测试
        rssFetchScheduler.scheduleFetch();

        // 验证结果
        verify(rssSourceRepository).findByActiveTrue();
        verify(rabbitTemplate).convertAndSend(
            eq("rss.fetch.exchange"),
            eq("rss.fetch.routing.key"),
            eq("source-1")
        );
        verify(metricsService).recordRssSourceFetchFailure();
    }
} 