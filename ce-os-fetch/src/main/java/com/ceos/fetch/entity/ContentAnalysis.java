package com.ceos.fetch.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Map;

@Data
@Entity
@Table(name = "content_analysis")
public class ContentAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "entry_id", nullable = false)
    private RssEntry entry;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @ElementCollection
    @CollectionTable(name = "content_keywords", joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "keyword")
    @Column(name = "weight")
    private Map<String, Double> keywords;

    @ElementCollection
    @CollectionTable(name = "content_categories", joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "category")
    @Column(name = "confidence")
    private Map<String, Double> categories;

    @Column(nullable = false)
    private String sentiment;

    @Column(name = "sentiment_score")
    private Double sentimentScore;

    @Column(name = "readability_score")
    private Double readabilityScore;

    @Column(name = "language")
    private String language;

    @Column(name = "processing_error")
    private String processingError;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
} 