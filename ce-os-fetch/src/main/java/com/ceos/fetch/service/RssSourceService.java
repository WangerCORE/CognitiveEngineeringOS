package com.ceos.fetch.service;

import com.ceos.fetch.dto.RssSourceDTO;
import com.ceos.fetch.entity.RssSource;
import com.ceos.fetch.repository.RssSourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RssSourceService {
    private final RssSourceRepository sourceRepository;

    @Transactional(readOnly = true)
    public Page<RssSourceDTO> findAll(Pageable pageable) {
        return sourceRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public RssSourceDTO findById(String id) {
        return sourceRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("RSS源不存在: " + id));
    }

    @Transactional
    public RssSourceDTO create(RssSourceDTO dto) {
        if (sourceRepository.existsByUrl(dto.getUrl())) {
            throw new IllegalArgumentException("URL已存在: " + dto.getUrl());
        }

        RssSource source = convertToEntity(dto);
        source = sourceRepository.save(source);
        return convertToDTO(source);
    }

    @Transactional
    public RssSourceDTO update(String id, RssSourceDTO dto) {
        RssSource source = sourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RSS源不存在: " + id));

        if (!source.getUrl().equals(dto.getUrl()) && sourceRepository.existsByUrl(dto.getUrl())) {
            throw new IllegalArgumentException("URL已存在: " + dto.getUrl());
        }

        updateEntity(source, dto);
        source = sourceRepository.save(source);
        return convertToDTO(source);
    }

    @Transactional
    public void delete(String id) {
        if (!sourceRepository.existsById(id)) {
            throw new EntityNotFoundException("RSS源不存在: " + id);
        }
        sourceRepository.deleteById(id);
    }

    private RssSourceDTO convertToDTO(RssSource source) {
        RssSourceDTO dto = new RssSourceDTO();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setUrl(source.getUrl());
        dto.setCategory(source.getCategory());
        dto.setCronExpression(source.getCronExpression());
        dto.setActive(source.isActive());
        dto.setDescription(source.getDescription());
        dto.setFetchIntervalMinutes(source.getFetchIntervalMinutes());
        return dto;
    }

    private RssSource convertToEntity(RssSourceDTO dto) {
        RssSource source = new RssSource();
        updateEntity(source, dto);
        return source;
    }

    private void updateEntity(RssSource source, RssSourceDTO dto) {
        source.setName(dto.getName());
        source.setUrl(dto.getUrl());
        source.setCategory(dto.getCategory());
        source.setCronExpression(dto.getCronExpression());
        source.setActive(dto.getActive());
        source.setDescription(dto.getDescription());
        source.setFetchIntervalMinutes(dto.getFetchIntervalMinutes());
    }
} 