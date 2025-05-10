package com.ceos.fetch.controller;

import com.ceos.fetch.entity.MarkdownFormatter;
import com.ceos.fetch.service.MarkdownFormatterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkdownFormatterControllerTest {

    @Mock
    private MarkdownFormatterService formatterService;

    @InjectMocks
    private MarkdownFormatterController formatterController;

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
    }

    @Test
    void getAllFormatters_ShouldReturnAllFormatters() {
        // 准备测试数据
        List<MarkdownFormatter> formatters = Arrays.asList(testFormatter);
        when(formatterService.getAllFormatters()).thenReturn(formatters);

        // 执行测试
        ResponseEntity<List<MarkdownFormatter>> response = formatterController.getAllFormatters();

        // 验证结果
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(formatters, response.getBody());
        verify(formatterService).getAllFormatters();
    }

    @Test
    void getActiveFormatters_ShouldReturnActiveFormatters() {
        // 准备测试数据
        List<MarkdownFormatter> formatters = Arrays.asList(testFormatter);
        when(formatterService.getActiveFormatters()).thenReturn(formatters);

        // 执行测试
        ResponseEntity<List<MarkdownFormatter>> response = formatterController.getActiveFormatters();

        // 验证结果
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(formatters, response.getBody());
        verify(formatterService).getActiveFormatters();
    }

    @Test
    void getFormatter_ShouldReturnFormatter() {
        // 准备测试数据
        when(formatterService.getFormatter("test-id")).thenReturn(testFormatter);

        // 执行测试
        ResponseEntity<MarkdownFormatter> response = formatterController.getFormatter("test-id");

        // 验证结果
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(testFormatter, response.getBody());
        verify(formatterService).getFormatter("test-id");
    }

    @Test
    void createFormatter_ShouldCreateFormatter() {
        // 准备测试数据
        when(formatterService.createFormatter(any(MarkdownFormatter.class))).thenReturn(testFormatter);

        // 执行测试
        ResponseEntity<MarkdownFormatter> response = formatterController.createFormatter(testFormatter);

        // 验证结果
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(testFormatter, response.getBody());
        verify(formatterService).createFormatter(testFormatter);
    }

    @Test
    void updateFormatter_ShouldUpdateFormatter() {
        // 准备测试数据
        when(formatterService.updateFormatter(eq("test-id"), any(MarkdownFormatter.class)))
            .thenReturn(testFormatter);

        // 执行测试
        ResponseEntity<MarkdownFormatter> response = formatterController.updateFormatter("test-id", testFormatter);

        // 验证结果
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(testFormatter, response.getBody());
        verify(formatterService).updateFormatter("test-id", testFormatter);
    }

    @Test
    void deleteFormatter_ShouldDeleteFormatter() {
        // 执行测试
        ResponseEntity<Void> response = formatterController.deleteFormatter("test-id");

        // 验证结果
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(formatterService).deleteFormatter("test-id");
    }

    @Test
    void formatContent_ShouldFormatContent() {
        // 准备测试数据
        Map<String, Object> request = new HashMap<>();
        request.put("content", "测试内容");
        Map<String, String> variables = new HashMap<>();
        variables.put("title", "测试标题");
        request.put("variables", variables);

        String formattedContent = "# 测试标题\n\n测试内容\n\n关键词：关键词1, 关键词2, 关键词3";
        when(formatterService.formatContent(anyString(), anyString(), anyMap()))
            .thenReturn(formattedContent);

        // 执行测试
        ResponseEntity<String> response = formatterController.formatContent("test-id", request);

        // 验证结果
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(formattedContent, response.getBody());
        verify(formatterService).formatContent(
            eq("测试内容"),
            eq("test-id"),
            eq(variables)
        );
    }
} 