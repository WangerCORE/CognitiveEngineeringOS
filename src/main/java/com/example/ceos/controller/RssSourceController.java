package com.example.ceos.controller;

import com.example.ceos.dto.ApiResponse;
import com.example.ceos.dto.RssSourceDTO;
import com.example.ceos.entity.SourceStatus;
import com.example.ceos.service.RssSourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sources")
@RequiredArgsConstructor
public class RssSourceController {
    private final RssSourceService rssSourceService;

    @GetMapping
    public ResponseEntity<ApiResponse> getSources(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) SourceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sort);
        Page<RssSourceDTO> sources = rssSourceService.getSources(query, status, pageRequest);
        return ResponseEntity.ok(new ApiResponse(true, "获取成功", sources));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSource(@PathVariable Long id) {
        RssSourceDTO source = rssSourceService.getSource(id);
        return ResponseEntity.ok(new ApiResponse(true, "获取成功", source));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createSource(
            @Valid @RequestBody RssSourceDTO sourceDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        RssSourceDTO created = rssSourceService.createSource(sourceDTO, userId);
        return ResponseEntity.ok(new ApiResponse(true, "创建成功", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSource(
            @PathVariable Long id,
            @Valid @RequestBody RssSourceDTO sourceDTO) {
        RssSourceDTO updated = rssSourceService.updateSource(id, sourceDTO);
        return ResponseEntity.ok(new ApiResponse(true, "更新成功", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSource(@PathVariable Long id) {
        rssSourceService.deleteSource(id);
        return ResponseEntity.ok(new ApiResponse(true, "删除成功"));
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<ApiResponse> testSource(@PathVariable Long id) {
        var result = rssSourceService.testSource(id);
        return ResponseEntity.ok(new ApiResponse(true, "测试完成", result));
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse> importSources(@RequestParam("file") MultipartFile file) {
        try {
            rssSourceService.importSources(file);
            return ResponseEntity.ok(new ApiResponse(true, "导入成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "导入失败: " + e.getMessage()));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportSources() {
        byte[] data = rssSourceService.exportSources();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rss-sources.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }
} 