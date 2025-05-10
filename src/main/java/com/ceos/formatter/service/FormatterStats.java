package com.ceos.formatter.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormatterStats {
    private long total;
    private long enabled;
    private long disabled;
    private long usageCount;
} 