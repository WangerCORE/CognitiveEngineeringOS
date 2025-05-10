package com.ceos.fetch.listener;

import com.ceos.fetch.service.MetricsService;
import com.ceos.fetch.service.RssFetchService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RssFetchListenerTest {

    @Mock
    private RssFetchService rssFetchService;

    @Mock
    private MetricsService metricsService;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private RssFetchListener rssFetchListener;

    @Test
    void handleFetchMessage_ShouldProcessMessageSuccessfully() {
        // 准备测试数据
        String sourceId = "test-source-id";

        // 模拟依赖行为
        when(metricsService.startRssSourceFetchTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(metricsService.getRegistry()).thenReturn(meterRegistry);

        // 执行测试
        rssFetchListener.handleFetchMessage(sourceId);

        // 验证结果
        verify(rssFetchService).fetchSource(sourceId);
        verify(metricsService).recordRssSourceFetchSuccess();
    }

    @Test
    void handleFetchMessage_WhenServiceThrowsException_ShouldPropagateException() {
        // 准备测试数据
        String sourceId = "test-source-id";

        // 模拟依赖行为
        when(metricsService.startRssSourceFetchTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(metricsService.getRegistry()).thenReturn(meterRegistry);
        doThrow(new RuntimeException("抓取失败"))
            .when(rssFetchService).fetchSource(anyString());

        // 执行测试并验证异常
        try {
            rssFetchListener.handleFetchMessage(sourceId);
        } catch (RuntimeException e) {
            // 验证结果
            verify(rssFetchService).fetchSource(sourceId);
            verify(metricsService).recordRssSourceFetchFailure();
            verify(metricsService, never()).recordRssSourceFetchSuccess();
        }
    }
} 