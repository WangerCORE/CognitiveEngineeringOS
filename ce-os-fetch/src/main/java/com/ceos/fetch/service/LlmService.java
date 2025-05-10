package com.ceos.fetch.service;

import com.ceos.fetch.config.LlmConfig;
import com.ceos.fetch.dto.llm.LlmRequest;
import com.ceos.fetch.dto.llm.LlmResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {
    private final LlmConfig llmConfig;
    private final RestTemplate restTemplate;
    private final MetricsService metricsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Retryable(
        value = {RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String generateSummary(String content) {
        Timer.Sample timer = metricsService.startLlmApiCallTimer();
        try {
            List<LlmRequest.Message> messages = new ArrayList<>();
            messages.add(LlmRequest.Message.system("你是一个专业的文章摘要生成助手。请为以下文章生成一个简洁的摘要，突出文章的主要观点和关键信息。"));
            messages.add(LlmRequest.Message.user(content));

            LlmRequest request = new LlmRequest();
            request.setModel(llmConfig.getModel());
            request.setMessages(messages);
            request.setMaxTokens(llmConfig.getMaxTokens());
            request.setTemperature(llmConfig.getTemperature());

            String response = callLlmApi(request);
            metricsService.recordLlmApiCallSuccess();
            return response;
        } catch (Exception e) {
            metricsService.recordLlmApiCallFailure();
            throw e;
        } finally {
            timer.stop(Timer.builder("llm.api.call.time")
                .tag("operation", "generateSummary")
                .register(metricsService.getRegistry()));
        }
    }

    @Retryable(
        value = {RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Map<String, Double> extractKeywords(String content) {
        Timer.Sample timer = metricsService.startLlmApiCallTimer();
        try {
            List<LlmRequest.Message> messages = new ArrayList<>();
            messages.add(LlmRequest.Message.system("你是一个关键词提取助手。请从以下文章中提取5-10个关键词，并给出每个关键词的重要性权重（0-1之间的小数）。请以JSON格式返回，格式为：{\"关键词1\": 权重1, \"关键词2\": 权重2, ...}"));
            messages.add(LlmRequest.Message.user(content));

            LlmRequest request = new LlmRequest();
            request.setModel(llmConfig.getModel());
            request.setMessages(messages);
            request.setMaxTokens(llmConfig.getMaxTokens());
            request.setTemperature(llmConfig.getTemperature());

            String response = callLlmApi(request);
            Map<String, Double> result = objectMapper.readValue(response, new TypeReference<Map<String, Double>>() {});
            metricsService.recordLlmApiCallSuccess();
            return result;
        } catch (Exception e) {
            metricsService.recordLlmApiCallFailure();
            throw new RuntimeException("解析关键词JSON失败: " + e.getMessage());
        } finally {
            timer.stop(Timer.builder("llm.api.call.time")
                .tag("operation", "extractKeywords")
                .register(metricsService.getRegistry()));
        }
    }

    @Retryable(
        value = {RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Map<String, Double> categorize(String content) {
        Timer.Sample timer = metricsService.startLlmApiCallTimer();
        try {
            List<LlmRequest.Message> messages = new ArrayList<>();
            messages.add(LlmRequest.Message.system("你是一个文章分类助手。请将以下文章分类到以下类别中：技术、商业、科学、文化、政治、教育、健康、娱乐。请给出每个类别的置信度（0-1之间的小数）。请以JSON格式返回，格式为：{\"类别1\": 置信度1, \"类别2\": 置信度2, ...}"));
            messages.add(LlmRequest.Message.user(content));

            LlmRequest request = new LlmRequest();
            request.setModel(llmConfig.getModel());
            request.setMessages(messages);
            request.setMaxTokens(llmConfig.getMaxTokens());
            request.setTemperature(llmConfig.getTemperature());

            String response = callLlmApi(request);
            Map<String, Double> result = objectMapper.readValue(response, new TypeReference<Map<String, Double>>() {});
            metricsService.recordLlmApiCallSuccess();
            return result;
        } catch (Exception e) {
            metricsService.recordLlmApiCallFailure();
            throw new RuntimeException("解析分类JSON失败: " + e.getMessage());
        } finally {
            timer.stop(Timer.builder("llm.api.call.time")
                .tag("operation", "categorize")
                .register(metricsService.getRegistry()));
        }
    }

    @Retryable(
        value = {RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String analyzeSentiment(String content) {
        Timer.Sample timer = metricsService.startLlmApiCallTimer();
        try {
            List<LlmRequest.Message> messages = new ArrayList<>();
            messages.add(LlmRequest.Message.system("你是一个情感分析助手。请分析以下文章的情感倾向，并给出情感标签（积极、消极、中性）和情感得分（-1到1之间的小数，-1表示最消极，1表示最积极）。请以JSON格式返回，格式为：{\"sentiment\": \"情感标签\", \"score\": 情感得分}"));
            messages.add(LlmRequest.Message.user(content));

            LlmRequest request = new LlmRequest();
            request.setModel(llmConfig.getModel());
            request.setMessages(messages);
            request.setMaxTokens(llmConfig.getMaxTokens());
            request.setTemperature(llmConfig.getTemperature());

            String response = callLlmApi(request);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            metricsService.recordLlmApiCallSuccess();
            return (String) result.get("sentiment");
        } catch (Exception e) {
            metricsService.recordLlmApiCallFailure();
            throw new RuntimeException("解析情感分析JSON失败: " + e.getMessage());
        } finally {
            timer.stop(Timer.builder("llm.api.call.time")
                .tag("operation", "analyzeSentiment")
                .register(metricsService.getRegistry()));
        }
    }

    @Retryable(
        value = {RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Double calculateReadability(String content) {
        Timer.Sample timer = metricsService.startLlmApiCallTimer();
        try {
            List<LlmRequest.Message> messages = new ArrayList<>();
            messages.add(LlmRequest.Message.system("你是一个可读性分析助手。请分析以下文章的可读性，并给出一个0-1之间的得分（1表示最容易理解）。请只返回数字。"));
            messages.add(LlmRequest.Message.user(content));

            LlmRequest request = new LlmRequest();
            request.setModel(llmConfig.getModel());
            request.setMessages(messages);
            request.setMaxTokens(llmConfig.getMaxTokens());
            request.setTemperature(llmConfig.getTemperature());

            String response = callLlmApi(request);
            Double result = Double.parseDouble(response.trim());
            metricsService.recordLlmApiCallSuccess();
            return result;
        } catch (Exception e) {
            metricsService.recordLlmApiCallFailure();
            throw new RuntimeException("解析可读性得分失败: " + e.getMessage());
        } finally {
            timer.stop(Timer.builder("llm.api.call.time")
                .tag("operation", "calculateReadability")
                .register(metricsService.getRegistry()));
        }
    }

    @Retryable(
        value = {RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String detectLanguage(String content) {
        Timer.Sample timer = metricsService.startLlmApiCallTimer();
        try {
            List<LlmRequest.Message> messages = new ArrayList<>();
            messages.add(LlmRequest.Message.system("你是一个语言检测助手。请检测以下文章的语言，并返回ISO 639-1语言代码（如zh、en、ja等）。请只返回语言代码。"));
            messages.add(LlmRequest.Message.user(content));

            LlmRequest request = new LlmRequest();
            request.setModel(llmConfig.getModel());
            request.setMessages(messages);
            request.setMaxTokens(llmConfig.getMaxTokens());
            request.setTemperature(llmConfig.getTemperature());

            String response = callLlmApi(request);
            metricsService.recordLlmApiCallSuccess();
            return response.trim();
        } catch (Exception e) {
            metricsService.recordLlmApiCallFailure();
            throw e;
        } finally {
            timer.stop(Timer.builder("llm.api.call.time")
                .tag("operation", "detectLanguage")
                .register(metricsService.getRegistry()));
        }
    }

    private String callLlmApi(LlmRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(llmConfig.getApiKey());

        HttpEntity<LlmRequest> entity = new HttpEntity<>(request, headers);

        try {
            LlmResponse response = restTemplate.postForObject(
                llmConfig.getApiUrl(),
                entity,
                LlmResponse.class
            );

            if (response != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            throw new RuntimeException("LLM API返回空响应");
        } catch (Exception e) {
            log.error("调用LLM API失败", e);
            throw new RuntimeException("调用LLM API失败: " + e.getMessage());
        }
    }
} 