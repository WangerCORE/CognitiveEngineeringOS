package com.ceos.fetch.controller;

import com.ceos.fetch.dto.ContentAnalysisDTO;
import com.ceos.fetch.service.ContentAnalysisService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentAnalysisController.class)
class ContentAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContentAnalysisService contentAnalysisService;

    @MockBean
    private MeterRegistry meterRegistry;

    @Test
    void getAnalysis_ShouldReturnAnalysis() throws Exception {
        // 准备测试数据
        ContentAnalysisDTO analysis = new ContentAnalysisDTO();
        analysis.setId("test-analysis-id");
        analysis.setEntryId("test-entry-id");
        analysis.setTitle("测试文章");
        analysis.setSummary("这是摘要");
        analysis.setKeywords(Map.of("测试", 0.8, "文章", 0.6));
        analysis.setCategories(Map.of("技术", 0.9, "科学", 0.3));
        analysis.setSentiment("积极");
        analysis.setSentimentScore(0.8);
        analysis.setReadabilityScore(0.7);
        analysis.setLanguage("zh");

        // 模拟服务行为
        when(contentAnalysisService.analyzeContent(anyString())).thenReturn(analysis);

        // 执行测试
        mockMvc.perform(get("/api/v1/content-analysis/{entryId}", "test-entry-id"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("test-analysis-id"))
            .andExpect(jsonPath("$.entryId").value("test-entry-id"))
            .andExpect(jsonPath("$.title").value("测试文章"))
            .andExpect(jsonPath("$.summary").value("这是摘要"))
            .andExpect(jsonPath("$.keywords.测试").value(0.8))
            .andExpect(jsonPath("$.keywords.文章").value(0.6))
            .andExpect(jsonPath("$.categories.技术").value(0.9))
            .andExpect(jsonPath("$.categories.科学").value(0.3))
            .andExpect(jsonPath("$.sentiment").value("积极"))
            .andExpect(jsonPath("$.sentimentScore").value(0.8))
            .andExpect(jsonPath("$.readabilityScore").value(0.7))
            .andExpect(jsonPath("$.language").value("zh"));
    }
} 