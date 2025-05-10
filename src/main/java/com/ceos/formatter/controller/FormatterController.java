package com.ceos.formatter.controller;

import com.ceos.formatter.entity.Formatter;
import com.ceos.formatter.entity.FormatterStatus;
import com.ceos.formatter.service.FormatterService;
import com.ceos.formatter.service.FormatterStats;
import com.ceos.formatter.service.FormatterTestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formatters")
@RequiredArgsConstructor
public class FormatterController {
    private final FormatterService formatterService;

    @GetMapping
    public ResponseEntity<Page<Formatter>> getFormatters(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) FormatterStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Formatter> formatters = formatterService.getFormatters(keyword, status, pageRequest);
        return ResponseEntity.ok(formatters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Formatter> getFormatter(@PathVariable Long id) {
        return ResponseEntity.ok(formatterService.getFormatter(id));
    }

    @PostMapping
    public ResponseEntity<Formatter> createFormatter(@RequestBody Formatter formatter) {
        return ResponseEntity.ok(formatterService.createFormatter(formatter));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Formatter> updateFormatter(
        @PathVariable Long id,
        @RequestBody Formatter formatter
    ) {
        formatter.setId(id);
        return ResponseEntity.ok(formatterService.updateFormatter(formatter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormatter(@PathVariable Long id) {
        formatterService.deleteFormatter(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteFormatters(@RequestBody List<Long> ids) {
        formatterService.batchDeleteFormatters(ids);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Formatter> updateStatus(
        @PathVariable Long id,
        @RequestBody FormatterStatus status
    ) {
        return ResponseEntity.ok(formatterService.updateStatus(id, status));
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<FormatterTestResult> testFormatter(@PathVariable Long id) {
        return ResponseEntity.ok(formatterService.testFormatter(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<FormatterStats> getStats() {
        return ResponseEntity.ok(formatterService.getStats());
    }
} 