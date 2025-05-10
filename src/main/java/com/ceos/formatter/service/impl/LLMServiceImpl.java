package com.ceos.formatter.service.impl;

import com.ceos.formatter.config.LLMConfig;
import com.ceos.formatter.exception.LLMServiceException;
import com.ceos.formatter.service.LLMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMServiceImpl implements LLMService {
    private final LLMConfig llmConfig;
    private final RestTemplate restTemplate;

    @Override
    public String formatContent(String prompt, String example) throws LLMServiceException {
        try {
            String fullPrompt = buildPrompt(prompt, example);
            Map<String, Object> requestBody = buildRequestBody(fullPrompt);
            HttpHeaders headers = buildHeaders();
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            Map<String, Object> response = restTemplate.postForObject(
                llmConfig.getApiEndpoint(),
                request,
                Map.class
            );
            
            return extractFormattedContent(response);
        } catch (Exception e) {
            log.error("LLM服务调用失败", e);
            throw new LLMServiceException("格式化失败: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String prompt, String example) {
        return String.format("""
            请按照以下要求格式化内容：
            
            要求：
            %s
            
            示例：
            %s
            
            请直接返回格式化后的内容，不要包含任何解释或说明。
            """, prompt, example);
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", llmConfig.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", llmConfig.getMaxTokens());
        requestBody.put("temperature", llmConfig.getTemperature());
        return requestBody;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + llmConfig.getApiKey());
        return headers;
    }

    private String extractFormattedContent(Map<String, Object> response) {
        if (response == null || !response.containsKey("choices")) {
            throw new LLMServiceException("无效的LLM响应");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> choice = ((java.util.List<Map<String, Object>>) response.get("choices")).get(0);
        return (String) choice.get("text");
    }
} 