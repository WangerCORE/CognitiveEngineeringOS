package com.ceos.fetch.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ContentAnalysisDTO {
    private String id;
    private String entryId;
    private String title;
    private String summary;
    private Map<String, Double> keywords;
    private Map<String, Double> categories;
    private String sentiment;
    private Double sentimentScore;
    private Double readabilityScore;
    private String language;
    private String processingError;
} 