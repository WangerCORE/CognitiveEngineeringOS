package com.ceos.formatter.service;

import com.ceos.formatter.entity.Formatter;
import com.ceos.formatter.entity.FormatterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FormatterService {
    Page<Formatter> getFormatters(String keyword, FormatterStatus status, Pageable pageable);
    
    Formatter getFormatter(Long id);
    
    Formatter createFormatter(Formatter formatter);
    
    Formatter updateFormatter(Formatter formatter);
    
    void deleteFormatter(Long id);
    
    void batchDeleteFormatters(List<Long> ids);
    
    Formatter updateStatus(Long id, FormatterStatus status);
    
    FormatterTestResult testFormatter(Long id);
    
    FormatterStats getStats();
} 