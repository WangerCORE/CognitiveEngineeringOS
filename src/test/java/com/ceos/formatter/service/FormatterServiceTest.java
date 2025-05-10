package com.ceos.formatter.service;

import com.ceos.formatter.entity.Formatter;
import com.ceos.formatter.entity.FormatterStatus;
import com.ceos.formatter.exception.FormatterNotFoundException;
import com.ceos.formatter.repository.FormatterRepository;
import com.ceos.formatter.service.impl.FormatterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FormatterServiceTest {

    @Autowired
    private FormatterService formatterService;

    @Autowired
    private FormatterRepository formatterRepository;

    private Formatter testFormatter;

    @BeforeEach
    void setUp() {
        testFormatter = new Formatter();
        testFormatter.setName("测试格式化器");
        testFormatter.setDescription("用于测试的格式化器");
        testFormatter.setPrompt("测试提示词");
        testFormatter.setExample("测试示例");
        testFormatter.setStatus(FormatterStatus.ENABLED);
    }

    @Test
    void testCreateFormatter() {
        Formatter savedFormatter = formatterService.createFormatter(testFormatter);
        assertNotNull(savedFormatter.getId());
        assertEquals(testFormatter.getName(), savedFormatter.getName());
    }

    @Test
    void testGetFormatter() {
        Formatter savedFormatter = formatterService.createFormatter(testFormatter);
        Formatter foundFormatter = formatterService.getFormatter(savedFormatter.getId());
        assertEquals(savedFormatter.getId(), foundFormatter.getId());
    }

    @Test
    void testGetFormatterNotFound() {
        assertThrows(FormatterNotFoundException.class, () -> formatterService.getFormatter(999L));
    }

    @Test
    void testUpdateFormatter() {
        Formatter savedFormatter = formatterService.createFormatter(testFormatter);
        savedFormatter.setName("更新后的名称");
        Formatter updatedFormatter = formatterService.updateFormatter(savedFormatter);
        assertEquals("更新后的名称", updatedFormatter.getName());
    }

    @Test
    void testDeleteFormatter() {
        Formatter savedFormatter = formatterService.createFormatter(testFormatter);
        formatterService.deleteFormatter(savedFormatter.getId());
        assertThrows(FormatterNotFoundException.class, () -> formatterService.getFormatter(savedFormatter.getId()));
    }

    @Test
    void testGetFormatters() {
        formatterService.createFormatter(testFormatter);
        Page<Formatter> formatters = formatterService.getFormatters(null, null, PageRequest.of(0, 10));
        assertTrue(formatters.getTotalElements() > 0);
    }

    @Test
    void testUpdateStatus() {
        Formatter savedFormatter = formatterService.createFormatter(testFormatter);
        Formatter updatedFormatter = formatterService.updateStatus(savedFormatter.getId(), FormatterStatus.DISABLED);
        assertEquals(FormatterStatus.DISABLED, updatedFormatter.getStatus());
    }

    @Test
    void testGetStats() {
        formatterService.createFormatter(testFormatter);
        FormatterStats stats = formatterService.getStats();
        assertTrue(stats.getTotal() > 0);
    }
} 