package com.ceos.fetch.service;

import com.ceos.fetch.entity.MarkdownFormatter;
import com.ceos.fetch.repository.MarkdownFormatterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownFormatterService {
    private final MarkdownFormatterRepository formatterRepository;
    private final MetricsService metricsService;
    private final LlmService llmService;

    @Transactional(readOnly = true)
    public List<MarkdownFormatter> getAllFormatters() {
        return formatterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MarkdownFormatter> getActiveFormatters() {
        return formatterRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public MarkdownFormatter getFormatter(String id) {
        return formatterRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("找不到ID为" + id + "的格式化器"));
    }

    @Transactional
    public MarkdownFormatter createFormatter(MarkdownFormatter formatter) {
        if (formatterRepository.existsByName(formatter.getName())) {
            throw new IllegalArgumentException("已存在名为" + formatter.getName() + "的格式化器");
        }
        return formatterRepository.save(formatter);
    }

    @Transactional
    public MarkdownFormatter updateFormatter(String id, MarkdownFormatter formatter) {
        MarkdownFormatter existing = getFormatter(id);
        existing.setName(formatter.getName());
        existing.setDescription(formatter.getDescription());
        existing.setTemplate(formatter.getTemplate());
        existing.setActive(formatter.isActive());
        return formatterRepository.save(existing);
    }

    @Transactional
    public void deleteFormatter(String id) {
        formatterRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public String formatContent(String content, String formatterId, Map<String, String> variables) {
        Timer.Sample timer = metricsService.startContentAnalysisTimer();
        try {
            MarkdownFormatter formatter = getFormatter(formatterId);
            String template = formatter.getTemplate();
            
            // 替换变量
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                template = template.replace("${" + entry.getKey() + "}", entry.getValue());
            }
            
            // 处理特殊标记
            template = processSpecialTags(template, content);
            
            metricsService.recordContentAnalysisSuccess();
            return template;
        } catch (Exception e) {
            log.error("格式化内容失败", e);
            metricsService.recordContentAnalysisFailure();
            throw e;
        } finally {
            timer.stop(Timer.builder("content.analysis.time")
                .tag("formatterId", formatterId)
                .register(metricsService.getRegistry()));
        }
    }

    private String processSpecialTags(String template, String content) {
        // 处理摘要标记
        Pattern summaryPattern = Pattern.compile("\\{\\{summary\\}\\}");
        Matcher summaryMatcher = summaryPattern.matcher(template);
        if (summaryMatcher.find()) {
            String summary = generateSummary(content);
            template = summaryMatcher.replaceAll(summary);
        }

        // 处理关键词标记
        Pattern keywordsPattern = Pattern.compile("\\{\\{keywords\\}\\}");
        Matcher keywordsMatcher = keywordsPattern.matcher(template);
        if (keywordsMatcher.find()) {
            String keywords = extractKeywords(content);
            template = keywordsMatcher.replaceAll(keywords);
        }

        return template;
    }

    private String generateSummary(String content) {
        try {
            String prompt = "请为以下内容生成一个简洁的摘要（不超过200字）：\n\n" + content;
            String response = llmService.generateText(prompt);
            return response.trim();
        } catch (Exception e) {
            log.error("生成摘要失败", e);
            metricsService.recordLlmApiCallFailure();
            return content.substring(0, Math.min(200, content.length())) + "...";
        }
    }

    private String extractKeywords(String content) {
        try {
            String prompt = "请从以下内容中提取3-5个关键词，用逗号分隔：\n\n" + content;
            String response = llmService.generateText(prompt);
            return response.trim();
        } catch (Exception e) {
            log.error("提取关键词失败", e);
            metricsService.recordLlmApiCallFailure();
            return "关键词1, 关键词2, 关键词3";
        }
    }
} 