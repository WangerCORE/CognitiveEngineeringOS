package com.ceos.formatter.exception;

public class FormatterNotFoundException extends RuntimeException {
    public FormatterNotFoundException(Long id) {
        super("格式化器未找到: " + id);
    }
} 