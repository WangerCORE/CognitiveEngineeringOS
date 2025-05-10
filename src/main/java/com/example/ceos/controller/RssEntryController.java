package com.example.ceos.controller;

import com.example.ceos.dto.ApiResponse;
import com.example.ceos.dto.RssEntryDTO;
import com.example.ceos.service.RssEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class RssEntryController {
    private final RssEntryService rssEntryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getEntries(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long sourceId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) Boolean isStarred,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "pubDate") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sort);
        Page<RssEntryDTO> entries = rssEntryService.getEntries(userId, query, sourceId, isRead, isStarred, pageRequest);
        return ResponseEntity.ok(new ApiResponse(true, "获取成功", entries));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEntry(@PathVariable Long id) {
        RssEntryDTO entry = rssEntryService.getEntry(id);
        return ResponseEntity.ok(new ApiResponse(true, "获取成功", entry));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long id) {
        RssEntryDTO entry = rssEntryService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse(true, "标记已读成功", entry));
    }

    @PostMapping("/{id}/unread")
    public ResponseEntity<ApiResponse> markAsUnread(@PathVariable Long id) {
        RssEntryDTO entry = rssEntryService.markAsUnread(id);
        return ResponseEntity.ok(new ApiResponse(true, "标记未读成功", entry));
    }

    @PostMapping("/{id}/star")
    public ResponseEntity<ApiResponse> toggleStar(@PathVariable Long id) {
        RssEntryDTO entry = rssEntryService.toggleStar(id);
        return ResponseEntity.ok(new ApiResponse(true, "收藏状态已更新", entry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEntry(@PathVariable Long id) {
        rssEntryService.deleteEntry(id);
        return ResponseEntity.ok(new ApiResponse(true, "删除成功"));
    }
} 