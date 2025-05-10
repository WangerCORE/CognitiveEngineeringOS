package com.ceos.fetch.service;

import com.ceos.fetch.entity.MarkdownFormatter;
import com.ceos.fetch.repository.MarkdownFormatterRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkdownFormatterServiceTest {

    @Mock
    private MarkdownFormatterRepository formatterRepository;

    @Mock
    private MetricsService metricsService;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private MarkdownFormatterService formatterService;

    private MarkdownFormatter testFormatter;

    @BeforeEach
    void setUp() {
        testFormatter = MarkdownFormatter.builder()
            .id("test-id")
            .name("测试格式化器")
            .description("用于测试的格式化器")
            .template("# ${title}\n\n{{summary}}\n\n关键词：{{keywords}}")
            .active(true)
            .build();

        when(metricsService.startContentAnalysisTimer())
            .thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(metricsService.getRegistry()).thenReturn(meterRegistry);
    }

    @Test
    void getAllFormatters_ShouldReturnAllFormatters() {
        // 准备测试数据
        List<MarkdownFormatter> formatters = Arrays.asList(testFormatter);
        when(formatterRepository.findAll()).thenReturn(formatters);

        // 执行测试
        List<MarkdownFormatter> result = formatterService.getAllFormatters();

        // 验证结果
        assertEquals(1, result.size());
        assertEquals(testFormatter, result.get(0));
        verify(formatterRepository).findAll();
    }

    @Test
    void getActiveFormatters_ShouldReturnActiveFormatters() {
        // 准备测试数据
        List<MarkdownFormatter> formatters = Arrays.asList(testFormatter);
        when(formatterRepository.findByActiveTrue()).thenReturn(formatters);

        // 执行测试
        List<MarkdownFormatter> result = formatterService.getActiveFormatters();

        // 验证结果
        assertEquals(1, result.size());
        assertEquals(testFormatter, result.get(0));
        verify(formatterRepository).findByActiveTrue();
    }

    @Test
    void getFormatter_WhenExists_ShouldReturnFormatter() {
        // 准备测试数据
        when(formatterRepository.findById("test-id")).thenReturn(Optional.of(testFormatter));

        // 执行测试
        MarkdownFormatter result = formatterService.getFormatter("test-id");

        // 验证结果
        assertEquals(testFormatter, result);
        verify(formatterRepository).findById("test-id");
    }

    @Test
    void getFormatter_WhenNotExists_ShouldThrowException() {
        // 准备测试数据
        when(formatterRepository.findById("non-existent")).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> formatterService.getFormatter("non-existent"));
        verify(formatterRepository).findById("non-existent");
    }

    @Test
    void createFormatter_WhenNameNotExists_ShouldCreateFormatter() {
        // 准备测试数据
        when(formatterRepository.existsByName(testFormatter.getName())).thenReturn(false);
        when(formatterRepository.save(any(MarkdownFormatter.class))).thenReturn(testFormatter);

        // 执行测试
        MarkdownFormatter result = formatterService.createFormatter(testFormatter);

        // 验证结果
        assertEquals(testFormatter, result);
        verify(formatterRepository).existsByName(testFormatter.getName());
        verify(formatterRepository).save(testFormatter);
    }

    @Test
    void createFormatter_WhenNameExists_ShouldThrowException() {
        // 准备测试数据
        when(formatterRepository.existsByName(testFormatter.getName())).thenReturn(true);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> formatterService.createFormatter(testFormatter));
        verify(formatterRepository).existsByName(testFormatter.getName());
        verify(formatterRepository, never()).save(any(MarkdownFormatter.class));
    }

    @Test
    void updateFormatter_WhenExists_ShouldUpdateFormatter() {
        // 准备测试数据
        when(formatterRepository.findById("test-id")).thenReturn(Optional.of(testFormatter));
        when(formatterRepository.save(any(MarkdownFormatter.class))).thenReturn(testFormatter);

        // 执行测试
        MarkdownFormatter result = formatterService.updateFormatter("test-id", testFormatter);

        // 验证结果
        assertEquals(testFormatter, result);
        verify(formatterRepository).findById("test-id");
        verify(formatterRepository).save(testFormatter);
    }

    @Test
    void deleteFormatter_ShouldDeleteFormatter() {
        // 执行测试
        formatterService.deleteFormatter("test-id");

        // 验证结果
        verify(formatterRepository).deleteById("test-id");
    }

    @Test
    void formatContent_ShouldFormatContentCorrectly() {
        // 准备测试数据
        when(formatterRepository.findById("test-id")).thenReturn(Optional.of(testFormatter));
        Map<String, String> variables = new HashMap<>();
        variables.put("title", "测试标题");
        String content = "这是一段测试内容，用于测试格式化功能。";

        // 执行测试
        String result = formatterService.formatContent(content, "test-id", variables);

        // 验证结果
        assertTrue(result.contains("# 测试标题"));
        assertTrue(result.contains("这是一段测试内容"));
        assertTrue(result.contains("关键词：关键词1, 关键词2, 关键词3"));
        verify(formatterRepository).findById("test-id");
        verify(metricsService).recordContentAnalysisSuccess();
    }

    @Test
    void formatContent_WhenFormatterNotFound_ShouldThrowException() {
        // 准备测试数据
        when(formatterRepository.findById("non-existent")).thenReturn(Optional.empty());
        Map<String, String> variables = new HashMap<>();

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, 
            () -> formatterService.formatContent("content", "non-existent", variables));
        verify(formatterRepository).findById("non-existent");
        verify(metricsService).recordContentAnalysisFailure();
    }
} 