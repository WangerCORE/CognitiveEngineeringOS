package com.ceos.fetch.service;

import com.ceos.fetch.dto.ContentAnalysisDTO;
import com.ceos.fetch.entity.ContentAnalysis;
import com.ceos.fetch.entity.RssEntry;
import com.ceos.fetch.repository.ContentAnalysisRepository;
import com.ceos.fetch.repository.RssEntryRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentAnalysisService {
    private final ContentAnalysisRepository contentAnalysisRepository;
    private final RssEntryRepository rssEntryRepository;
    private final LlmService llmService;
    private final MetricsService metricsService;

    @Transactional
    public ContentAnalysisDTO analyzeContent(String entryId) {
        log.info("开始分析内容，entryId: {}", entryId);
        Timer.Sample timer = metricsService.startContentAnalysisTimer();
        
        try {
            RssEntry entry = rssEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("RSS条目不存在: " + entryId));

            // 检查是否已经分析过
            if (contentAnalysisRepository.existsByEntryId(entryId)) {
                log.info("内容已经分析过，直接返回结果");
                return convertToDTO(contentAnalysisRepository.findByEntryId(entryId).orElseThrow());
            }

            ContentAnalysis analysis = new ContentAnalysis();
            analysis.setEntryId(entryId);
            analysis.setTitle(entry.getTitle());

            // 生成摘要
            String summary = llmService.generateSummary(entry.getContent());
            analysis.setSummary(summary);

            // 提取关键词
            Map<String, Double> keywords = llmService.extractKeywords(entry.getContent());
            analysis.setKeywords(keywords);

            // 分类
            Map<String, Double> categories = llmService.categorize(entry.getContent());
            analysis.setCategories(categories);

            // 情感分析
            String sentiment = llmService.analyzeSentiment(entry.getContent());
            analysis.setSentiment(sentiment);

            // 可读性分析
            Double readabilityScore = llmService.calculateReadability(entry.getContent());
            analysis.setReadabilityScore(readabilityScore);

            // 语言检测
            String language = llmService.detectLanguage(entry.getContent());
            analysis.setLanguage(language);

            ContentAnalysis savedAnalysis = contentAnalysisRepository.save(analysis);
            log.info("内容分析完成，entryId: {}", entryId);
            
            metricsService.recordContentAnalysisSuccess();
            return convertToDTO(savedAnalysis);
        } catch (Exception e) {
            log.error("内容分析失败", e);
            ContentAnalysis analysis = new ContentAnalysis();
            analysis.setEntryId(entryId);
            analysis.setTitle(entry.getTitle());
            analysis.setProcessingError("分析失败: " + e.getMessage());
            ContentAnalysis savedAnalysis = contentAnalysisRepository.save(analysis);
            metricsService.recordContentAnalysisFailure();
            return convertToDTO(savedAnalysis);
        } finally {
            timer.stop(Timer.builder("content.analysis.time")
                .tag("entryId", entryId)
                .register(metricsService.getRegistry()));
        }
    }

    private ContentAnalysisDTO convertToDTO(ContentAnalysis analysis) {
        ContentAnalysisDTO dto = new ContentAnalysisDTO();
        dto.setId(analysis.getId());
        dto.setEntryId(analysis.getEntryId());
        dto.setTitle(analysis.getTitle());
        dto.setSummary(analysis.getSummary());
        dto.setKeywords(analysis.getKeywords());
        dto.setCategories(analysis.getCategories());
        dto.setSentiment(analysis.getSentiment());
        dto.setSentimentScore(analysis.getSentimentScore());
        dto.setReadabilityScore(analysis.getReadabilityScore());
        dto.setLanguage(analysis.getLanguage());
        dto.setProcessingError(analysis.getProcessingError());
        return dto;
    }
} 