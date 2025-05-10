package com.ceos.fetch.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rss_entries")
public class RssEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private RssSource source;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String link;

    @Column
    private String author;

    @Column
    private LocalDateTime publishedDate;

    @Column(nullable = false)
    private String guid;

    @Column(nullable = false)
    private boolean processed = false;

    @Column
    private String processingError;

    @CreationTimestamp
    private LocalDateTime createdAt;
} 