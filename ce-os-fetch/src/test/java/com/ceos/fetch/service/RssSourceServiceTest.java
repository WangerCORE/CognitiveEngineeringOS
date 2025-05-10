package com.ceos.fetch.service;

import com.ceos.fetch.dto.RssSourceDTO;
import com.ceos.fetch.entity.RssSource;
import com.ceos.fetch.repository.RssSourceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RssSourceServiceTest {

    @Mock
    private RssSourceRepository rssSourceRepository;

    @InjectMocks
    private RssSourceService rssSourceService;

    private RssSource testSource;
    private RssSourceDTO testSourceDTO;

    @BeforeEach
    void setUp() {
        testSource = new RssSource();
        testSource.setId(UUID.randomUUID().toString());
        testSource.setName("Test Source");
        testSource.setUrl("https://example.com/rss");
        testSource.setCategory("Test");
        testSource.setCronExpression("0 0 * * * ?");
        testSource.setActive(true);
        testSource.setDescription("Test Description");
        testSource.setFetchIntervalMinutes(30);

        testSourceDTO = new RssSourceDTO();
        testSourceDTO.setName("Test Source");
        testSourceDTO.setUrl("https://example.com/rss");
        testSourceDTO.setCategory("Test");
        testSourceDTO.setCronExpression("0 0 * * * ?");
        testSourceDTO.setActive(true);
        testSourceDTO.setDescription("Test Description");
        testSourceDTO.setFetchIntervalMinutes(30);
    }

    @Test
    void findAll_ShouldReturnPageOfSources() {
        // Arrange
        List<RssSource> sources = Arrays.asList(testSource);
        Page<RssSource> sourcePage = new PageImpl<>(sources);
        Pageable pageable = PageRequest.of(0, 10);
        when(rssSourceRepository.findAll(pageable)).thenReturn(sourcePage);

        // Act
        Page<RssSourceDTO> result = rssSourceService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testSource.getName(), result.getContent().get(0).getName());
        verify(rssSourceRepository).findAll(pageable);
    }

    @Test
    void findById_WhenSourceExists_ShouldReturnSource() {
        // Arrange
        when(rssSourceRepository.findById(testSource.getId())).thenReturn(Optional.of(testSource));

        // Act
        RssSourceDTO result = rssSourceService.findById(testSource.getId());

        // Assert
        assertNotNull(result);
        assertEquals(testSource.getName(), result.getName());
        verify(rssSourceRepository).findById(testSource.getId());
    }

    @Test
    void findById_WhenSourceDoesNotExist_ShouldThrowException() {
        // Arrange
        when(rssSourceRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> rssSourceService.findById("non-existent-id"));
        verify(rssSourceRepository).findById("non-existent-id");
    }

    @Test
    void create_WhenUrlDoesNotExist_ShouldCreateSource() {
        // Arrange
        when(rssSourceRepository.existsByUrl(testSourceDTO.getUrl())).thenReturn(false);
        when(rssSourceRepository.save(any(RssSource.class))).thenReturn(testSource);

        // Act
        RssSourceDTO result = rssSourceService.create(testSourceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testSource.getName(), result.getName());
        verify(rssSourceRepository).existsByUrl(testSourceDTO.getUrl());
        verify(rssSourceRepository).save(any(RssSource.class));
    }

    @Test
    void create_WhenUrlExists_ShouldThrowException() {
        // Arrange
        when(rssSourceRepository.existsByUrl(testSourceDTO.getUrl())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rssSourceService.create(testSourceDTO));
        verify(rssSourceRepository).existsByUrl(testSourceDTO.getUrl());
        verify(rssSourceRepository, never()).save(any(RssSource.class));
    }

    @Test
    void update_WhenSourceExists_ShouldUpdateSource() {
        // Arrange
        when(rssSourceRepository.findById(testSource.getId())).thenReturn(Optional.of(testSource));
        when(rssSourceRepository.save(any(RssSource.class))).thenReturn(testSource);

        // Act
        RssSourceDTO result = rssSourceService.update(testSource.getId(), testSourceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testSource.getName(), result.getName());
        verify(rssSourceRepository).findById(testSource.getId());
        verify(rssSourceRepository).save(any(RssSource.class));
    }

    @Test
    void update_WhenSourceDoesNotExist_ShouldThrowException() {
        // Arrange
        when(rssSourceRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> rssSourceService.update("non-existent-id", testSourceDTO));
        verify(rssSourceRepository).findById("non-existent-id");
        verify(rssSourceRepository, never()).save(any(RssSource.class));
    }

    @Test
    void delete_WhenSourceExists_ShouldDeleteSource() {
        // Arrange
        when(rssSourceRepository.existsById(testSource.getId())).thenReturn(true);
        doNothing().when(rssSourceRepository).deleteById(testSource.getId());

        // Act
        rssSourceService.delete(testSource.getId());

        // Assert
        verify(rssSourceRepository).existsById(testSource.getId());
        verify(rssSourceRepository).deleteById(testSource.getId());
    }

    @Test
    void delete_WhenSourceDoesNotExist_ShouldThrowException() {
        // Arrange
        when(rssSourceRepository.existsById(any())).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> rssSourceService.delete("non-existent-id"));
        verify(rssSourceRepository).existsById("non-existent-id");
        verify(rssSourceRepository, never()).deleteById(any());
    }
} 