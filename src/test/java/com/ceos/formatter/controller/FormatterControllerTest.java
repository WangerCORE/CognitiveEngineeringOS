package com.ceos.formatter.controller;

import com.ceos.formatter.entity.Formatter;
import com.ceos.formatter.entity.FormatterStatus;
import com.ceos.formatter.service.FormatterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FormatterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FormatterService formatterService;

    private Formatter testFormatter;

    @BeforeEach
    void setUp() {
        testFormatter = new Formatter();
        testFormatter.setId(1L);
        testFormatter.setName("测试格式化器");
        testFormatter.setDescription("用于测试的格式化器");
        testFormatter.setPrompt("测试提示词");
        testFormatter.setExample("测试示例");
        testFormatter.setStatus(FormatterStatus.ENABLED);
    }

    @Test
    void testGetFormatters() throws Exception {
        Page<Formatter> formatterPage = new PageImpl<>(Collections.singletonList(testFormatter));
        when(formatterService.getFormatters(any(), any(), any())).thenReturn(formatterPage);

        mockMvc.perform(get("/api/formatters"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value(testFormatter.getName()));
    }

    @Test
    void testGetFormatter() throws Exception {
        when(formatterService.getFormatter(1L)).thenReturn(testFormatter);

        mockMvc.perform(get("/api/formatters/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(testFormatter.getName()));
    }

    @Test
    void testCreateFormatter() throws Exception {
        when(formatterService.createFormatter(any())).thenReturn(testFormatter);

        mockMvc.perform(post("/api/formatters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFormatter)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(testFormatter.getName()));
    }

    @Test
    void testUpdateFormatter() throws Exception {
        when(formatterService.updateFormatter(any())).thenReturn(testFormatter);

        mockMvc.perform(put("/api/formatters/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFormatter)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(testFormatter.getName()));
    }

    @Test
    void testDeleteFormatter() throws Exception {
        mockMvc.perform(delete("/api/formatters/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatus() throws Exception {
        when(formatterService.updateStatus(1L, FormatterStatus.DISABLED)).thenReturn(testFormatter);

        mockMvc.perform(patch("/api/formatters/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"DISABLED\""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(FormatterStatus.ENABLED.name()));
    }

    @Test
    void testGetStats() throws Exception {
        mockMvc.perform(get("/api/formatters/stats"))
            .andExpect(status().isOk());
    }
} 