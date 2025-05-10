package com.ceos.fetch.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rss_sources")
public class RssSource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String cronExpression;

    @Column(nullable = false)
    private boolean active = true;

    @Column
    private String description;

    @Column
    private String lastFetchError;

    @Column
    private LocalDateTime lastFetchTime;

    @Column
    private Integer fetchIntervalMinutes = 60;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
} 