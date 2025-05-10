package com.ceos.formatter.service.impl;

import com.ceos.formatter.entity.Formatter;
import com.ceos.formatter.entity.FormatterStatus;
import com.ceos.formatter.exception.FormatterNotFoundException;
import com.ceos.formatter.repository.FormatterRepository;
import com.ceos.formatter.service.FormatterService;
import com.ceos.formatter.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormatterServiceImpl implements FormatterService {
    private final FormatterRepository formatterRepository;
    private final LLMService llmService;

    @Override
    public Page<Formatter> getFormatters(String keyword, FormatterStatus status, Pageable pageable) {
        return formatterRepository.findByKeywordAndStatus(keyword, status, pageable);
    }

    @Override
    public Formatter getFormatter(Long id) {
        return formatterRepository.findById(id)
            .orElseThrow(() -> new FormatterNotFoundException(id));
    }

    @Override
    @Transactional
    public Formatter createFormatter(Formatter formatter) {
        return formatterRepository.save(formatter);
    }

    @Override
    @Transactional
    public Formatter updateFormatter(Formatter formatter) {
        Formatter existingFormatter = getFormatter(formatter.getId());
        existingFormatter.setName(formatter.getName());
        existingFormatter.setDescription(formatter.getDescription());
        existingFormatter.setPrompt(formatter.getPrompt());
        existingFormatter.setExample(formatter.getExample());
        return formatterRepository.save(existingFormatter);
    }

    @Override
    @Transactional
    public void deleteFormatter(Long id) {
        formatterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDeleteFormatters(List<Long> ids) {
        formatterRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public Formatter updateStatus(Long id, FormatterStatus status) {
        Formatter formatter = getFormatter(id);
        formatter.setStatus(status);
        return formatterRepository.save(formatter);
    }

    @Override
    @Transactional
    public FormatterTestResult testFormatter(Long id) {
        Formatter formatter = getFormatter(id);
        try {
            String formattedContent = llmService.formatContent(
                formatter.getPrompt(),
                formatter.getExample()
            );
            
            // 更新使用统计
            formatter.setLastUsedTime(LocalDateTime.now());
            formatter.setUsageCount(formatter.getUsageCount() + 1);
            formatterRepository.save(formatter);
            
            return new FormatterTestResult(true, "格式化成功", formattedContent);
        } catch (Exception e) {
            return new FormatterTestResult(false, "格式化失败: " + e.getMessage(), null);
        }
    }

    @Override
    public FormatterStats getStats() {
        long total = formatterRepository.count();
        long enabled = formatterRepository.countByStatus(FormatterStatus.ENABLED);
        long disabled = formatterRepository.countByStatus(FormatterStatus.DISABLED);
        long usageCount = formatterRepository.sumUsageCount();
        
        return new FormatterStats(total, enabled, disabled, usageCount);
    }
} 