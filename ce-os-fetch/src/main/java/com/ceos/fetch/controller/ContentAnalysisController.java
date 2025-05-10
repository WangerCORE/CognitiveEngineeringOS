package com.ceos.fetch.controller;

import com.ceos.fetch.dto.ContentAnalysisDTO;
import com.ceos.fetch.service.ContentAnalysisService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/content-analysis")
@RequiredArgsConstructor
public class ContentAnalysisController {
    private final ContentAnalysisService contentAnalysisService;
    private final MeterRegistry meterRegistry;

    @GetMapping("/{entryId}")
    public ResponseEntity<ContentAnalysisDTO> getAnalysis(@PathVariable String entryId) {
        log.info("获取内容分析结果，entryId: {}", entryId);
        ContentAnalysisDTO analysis = contentAnalysisService.analyzeContent(entryId);
        return ResponseEntity.ok(analysis);
    }
} 