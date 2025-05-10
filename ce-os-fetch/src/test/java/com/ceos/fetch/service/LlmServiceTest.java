package com.ceos.fetch.service;

import com.ceos.fetch.config.LlmConfig;
import com.ceos.fetch.dto.llm.LlmRequest;
import com.ceos.fetch.dto.llm.LlmResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmServiceTest {

    @Mock
    private LlmConfig llmConfig;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private LlmService llmService;

    @BeforeEach
    void setUp() {
        when(llmConfig.getModel()).thenReturn("gpt-3.5-turbo");
        when(llmConfig.getMaxTokens()).thenReturn(2000);
        when(llmConfig.getTemperature()).thenReturn(0.7);
        when(llmConfig.getApiKey()).thenReturn("test-api-key");
        when(llmConfig.getApiUrl()).thenReturn("https://api.openai.com/v1/chat/completions");
    }

    @Test
    void generateSummary_ShouldReturnSummary() {
        // 准备测试数据
        String content = "这是一篇测试文章。";
        LlmResponse response = new LlmResponse();
        LlmResponse.Choice choice = new LlmResponse.Choice();
        LlmResponse.Message message = new LlmResponse.Message();
        message.setContent("这是文章的摘要。");
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        // 模拟API调用
        when(restTemplate.postForObject(
            eq(llmConfig.getApiUrl()),
            any(HttpEntity.class),
            eq(LlmResponse.class)
        )).thenReturn(response);

        // 执行测试
        String summary = llmService.generateSummary(content);

        // 验证结果
        assertNotNull(summary);
        assertEquals("这是文章的摘要。", summary);
    }

    @Test
    void extractKeywords_ShouldReturnKeywords() {
        // 准备测试数据
        String content = "这是一篇测试文章。";
        LlmResponse response = new LlmResponse();
        LlmResponse.Choice choice = new LlmResponse.Choice();
        LlmResponse.Message message = new LlmResponse.Message();
        message.setContent("{\"测试\": 0.8, \"文章\": 0.6}");
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        // 模拟API调用
        when(restTemplate.postForObject(
            eq(llmConfig.getApiUrl()),
            any(HttpEntity.class),
            eq(LlmResponse.class)
        )).thenReturn(response);

        // 执行测试
        Map<String, Double> keywords = llmService.extractKeywords(content);

        // 验证结果
        assertNotNull(keywords);
        assertEquals(2, keywords.size());
        assertEquals(0.8, keywords.get("测试"));
        assertEquals(0.6, keywords.get("文章"));
    }

    @Test
    void categorize_ShouldReturnCategories() {
        // 准备测试数据
        String content = "这是一篇技术文章。";
        LlmResponse response = new LlmResponse();
        LlmResponse.Choice choice = new LlmResponse.Choice();
        LlmResponse.Message message = new LlmResponse.Message();
        message.setContent("{\"技术\": 0.9, \"科学\": 0.3}");
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        // 模拟API调用
        when(restTemplate.postForObject(
            eq(llmConfig.getApiUrl()),
            any(HttpEntity.class),
            eq(LlmResponse.class)
        )).thenReturn(response);

        // 执行测试
        Map<String, Double> categories = llmService.categorize(content);

        // 验证结果
        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals(0.9, categories.get("技术"));
        assertEquals(0.3, categories.get("科学"));
    }

    @Test
    void analyzeSentiment_ShouldReturnSentiment() {
        // 准备测试数据
        String content = "这是一篇积极向上的文章。";
        LlmResponse response = new LlmResponse();
        LlmResponse.Choice choice = new LlmResponse.Choice();
        LlmResponse.Message message = new LlmResponse.Message();
        message.setContent("{\"sentiment\": \"积极\", \"score\": 0.8}");
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        // 模拟API调用
        when(restTemplate.postForObject(
            eq(llmConfig.getApiUrl()),
            any(HttpEntity.class),
            eq(LlmResponse.class)
        )).thenReturn(response);

        // 执行测试
        String sentiment = llmService.analyzeSentiment(content);

        // 验证结果
        assertNotNull(sentiment);
        assertEquals("积极", sentiment);
    }

    @Test
    void calculateReadability_ShouldReturnScore() {
        // 准备测试数据
        String content = "这是一篇简单的文章。";
        LlmResponse response = new LlmResponse();
        LlmResponse.Choice choice = new LlmResponse.Choice();
        LlmResponse.Message message = new LlmResponse.Message();
        message.setContent("0.8");
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        // 模拟API调用
        when(restTemplate.postForObject(
            eq(llmConfig.getApiUrl()),
            any(HttpEntity.class),
            eq(LlmResponse.class)
        )).thenReturn(response);

        // 执行测试
        Double score = llmService.calculateReadability(content);

        // 验证结果
        assertNotNull(score);
        assertEquals(0.8, score);
    }

    @Test
    void detectLanguage_ShouldReturnLanguageCode() {
        // 准备测试数据
        String content = "这是一篇中文文章。";
        LlmResponse response = new LlmResponse();
        LlmResponse.Choice choice = new LlmResponse.Choice();
        LlmResponse.Message message = new LlmResponse.Message();
        message.setContent("zh");
        choice.setMessage(message);
        response.setChoices(List.of(choice));

        // 模拟API调用
        when(restTemplate.postForObject(
            eq(llmConfig.getApiUrl()),
            any(HttpEntity.class),
            eq(LlmResponse.class)
        )).thenReturn(response);

        // 执行测试
        String language = llmService.detectLanguage(content);

        // 验证结果
        assertNotNull(language);
        assertEquals("zh", language);
    }
} 