package com.ceos.fetch.service;

import com.ceos.fetch.entity.RssSource;
import com.ceos.fetch.repository.RssEntryRepository;
import com.ceos.fetch.repository.RssSourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class RssFetchServiceTest {

    @Autowired
    private RssFetchService rssFetchService;

    @MockBean
    private RssSourceRepository sourceRepository;

    @MockBean
    private RssEntryRepository entryRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void testFetchAllActiveSources() {
        // 准备测试数据
        RssSource source1 = new RssSource();
        source1.setId("1");
        source1.setUrl("https://example.com/feed1.xml");
        source1.setActive(true);

        RssSource source2 = new RssSource();
        source2.setId("2");
        source2.setUrl("https://example.com/feed2.xml");
        source2.setActive(true);

        when(sourceRepository.findByActiveTrue())
            .thenReturn(Arrays.asList(source1, source2));

        // 执行测试
        rssFetchService.fetchAllActiveSources();

        // 验证结果
        verify(sourceRepository, times(1)).findByActiveTrue();
        verify(sourceRepository, times(2)).save(any(RssSource.class));
    }
} 