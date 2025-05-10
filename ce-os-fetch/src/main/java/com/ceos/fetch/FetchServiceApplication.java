package com.ceos.fetch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FetchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FetchServiceApplication.class, args);
    }
} 