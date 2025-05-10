package com.example.ceos.service;

import com.example.ceos.dto.RssEntryDTO;
import com.example.ceos.entity.RssEntry;
import com.example.ceos.exception.ResourceNotFoundException;
import com.example.ceos.repository.RssEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RssEntryService {
    private final RssEntryRepository rssEntryRepository;

    public Page<RssEntryDTO> getEntries(
            Long userId,
            String query,
            Long sourceId,
            Boolean isRead,
            Boolean isStarred,
            Pageable pageable) {
        return rssEntryRepository.findBySearchCriteria(userId, query, sourceId, isRead, isStarred, pageable)
                .map(this::convertToDTO);
    }

    public RssEntryDTO getEntry(Long id) {
        return rssEntryRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("RSS条目", "id", id));
    }

    @Transactional
    public RssEntryDTO markAsRead(Long id) {
        RssEntry entry = rssEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RSS条目", "id", id));
        entry.setRead(true);
        return convertToDTO(rssEntryRepository.save(entry));
    }

    @Transactional
    public RssEntryDTO markAsUnread(Long id) {
        RssEntry entry = rssEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RSS条目", "id", id));
        entry.setRead(false);
        return convertToDTO(rssEntryRepository.save(entry));
    }

    @Transactional
    public RssEntryDTO toggleStar(Long id) {
        RssEntry entry = rssEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RSS条目", "id", id));
        entry.setStarred(!entry.isStarred());
        return convertToDTO(rssEntryRepository.save(entry));
    }

    @Transactional
    public void deleteEntry(Long id) {
        if (!rssEntryRepository.existsById(id)) {
            throw new ResourceNotFoundException("RSS条目", "id", id);
        }
        rssEntryRepository.deleteById(id);
    }

    private RssEntryDTO convertToDTO(RssEntry entry) {
        RssEntryDTO dto = new RssEntryDTO();
        dto.setId(entry.getId());
        dto.setTitle(entry.getTitle());
        dto.setDescription(entry.getDescription());
        dto.setLink(entry.getLink());
        dto.setGuid(entry.getGuid());
        dto.setPubDate(entry.getPubDate());
        dto.setAuthor(entry.getAuthor());
        dto.setContent(entry.getContent());
        dto.setRead(entry.isRead());
        dto.setStarred(entry.isStarred());
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setSourceId(entry.getSource().getId());
        dto.setSourceName(entry.getSource().getName());
        dto.setSourceUrl(entry.getSource().getUrl());
        return dto;
    }
} 