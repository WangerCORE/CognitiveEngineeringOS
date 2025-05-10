package com.ceos.formatter.service;

public interface LLMService {
    /**
     * 格式化内容
     * @param prompt 提示词
     * @param example 示例内容
     * @return 格式化后的内容
     */
    String formatContent(String prompt, String example) throws LLMServiceException;
} 