package com.example.ceos.entity;

public enum SourceStatus {
    ACTIVE("正常"),
    ERROR("错误"),
    DISABLED("禁用");

    private final String description;

    SourceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 