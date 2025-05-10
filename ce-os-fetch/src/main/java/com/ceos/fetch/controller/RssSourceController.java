package com.ceos.fetch.controller;

import com.ceos.fetch.dto.RssSourceDTO;
import com.ceos.fetch.service.RssSourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rss-sources")
@RequiredArgsConstructor
public class RssSourceController {
    private final RssSourceService sourceService;

    @GetMapping
    public ResponseEntity<Page<RssSourceDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(sourceService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RssSourceDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(sourceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RssSourceDTO> create(@Valid @RequestBody RssSourceDTO dto) {
        return ResponseEntity.ok(sourceService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RssSourceDTO> update(
            @PathVariable String id,
            @Valid @RequestBody RssSourceDTO dto) {
        return ResponseEntity.ok(sourceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sourceService.delete(id);
        return ResponseEntity.ok().build();
    }
} 