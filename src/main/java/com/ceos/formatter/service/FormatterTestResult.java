package com.ceos.formatter.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormatterTestResult {
    private boolean success;
    private String message;
    private String formattedContent;
} 