package com.example.ceos.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RssEntryDTO {
    private Long id;
    private String title;
    private String description;
    private String link;
    private String guid;
    private LocalDateTime pubDate;
    private String author;
    private String content;
    private boolean isRead;
    private boolean isStarred;
    private LocalDateTime createdAt;
    private Long sourceId;
    private String sourceName;
    private String sourceUrl;
} 