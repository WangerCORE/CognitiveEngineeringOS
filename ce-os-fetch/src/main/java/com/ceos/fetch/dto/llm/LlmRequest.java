package com.ceos.fetch.dto.llm;

import lombok.Data;

import java.util.List;

@Data
public class LlmRequest {
    private String model;
    private List<Message> messages;
    private Integer maxTokens;
    private Double temperature;

    @Data
    public static class Message {
        private String role;
        private String content;

        public static Message system(String content) {
            Message message = new Message();
            message.setRole("system");
            message.setContent(content);
            return message;
        }

        public static Message user(String content) {
            Message message = new Message();
            message.setRole("user");
            message.setContent(content);
            return message;
        }
    }
} 