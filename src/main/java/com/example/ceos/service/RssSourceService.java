package com.example.ceos.service;

import com.example.ceos.dto.RssSourceDTO;
import com.example.ceos.entity.RssSource;
import com.example.ceos.entity.SourceStatus;
import com.example.ceos.entity.User;
import com.example.ceos.exception.BadRequestException;
import com.example.ceos.exception.ResourceNotFoundException;
import com.example.ceos.repository.RssSourceRepository;
import com.example.ceos.repository.UserRepository;
import com.example.ceos.util.JsonUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RssSourceService {
    private final RssSourceRepository rssSourceRepository;
    private final UserRepository userRepository;

    public Page<RssSourceDTO> getSources(String query, SourceStatus status, Pageable pageable) {
        return rssSourceRepository.findBySearchCriteria(query, status, pageable)
                .map(this::convertToDTO);
    }

    public RssSourceDTO getSource(Long id) {
        return rssSourceRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("RSS源", "id", id));
    }

    @Transactional
    public RssSourceDTO createSource(RssSourceDTO sourceDTO, Long userId) {
        if (rssSourceRepository.existsByUrl(sourceDTO.getUrl())) {
            throw new BadRequestException("该URL已存在");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));

        RssSource source = new RssSource();
        source.setName(sourceDTO.getName());
        source.setUrl(sourceDTO.getUrl());
        source.setCategory(sourceDTO.getCategory());
        source.setDescription(sourceDTO.getDescription());
        source.setUpdateInterval(sourceDTO.getUpdateInterval());
        source.setUser(user);

        return convertToDTO(rssSourceRepository.save(source));
    }

    @Transactional
    public RssSourceDTO updateSource(Long id, RssSourceDTO sourceDTO) {
        RssSource source = rssSourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RSS源", "id", id));

        if (!source.getUrl().equals(sourceDTO.getUrl()) &&
                rssSourceRepository.existsByUrl(sourceDTO.getUrl())) {
            throw new BadRequestException("该URL已存在");
        }

        source.setName(sourceDTO.getName());
        source.setUrl(sourceDTO.getUrl());
        source.setCategory(sourceDTO.getCategory());
        source.setDescription(sourceDTO.getDescription());
        source.setUpdateInterval(sourceDTO.getUpdateInterval());

        return convertToDTO(rssSourceRepository.save(source));
    }

    @Transactional
    public void deleteSource(Long id) {
        if (!rssSourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("RSS源", "id", id);
        }
        rssSourceRepository.deleteById(id);
    }

    public Map<String, Object> testSource(Long id) {
        RssSource source = rssSourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RSS源", "id", id));

        try {
            URL feedUrl = new URL(source.getUrl());
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            List<Map<String, Object>> items = feed.getEntries().stream()
                    .limit(5)
                    .map(this::convertEntryToMap)
                    .collect(Collectors.toList());

            return Map.of(
                    "success", true,
                    "message", "RSS源测试成功",
                    "items", items
            );
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "RSS源测试失败: " + e.getMessage()
            );
        }
    }

    @Transactional
    public void importSources(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("文件不能为空");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String json = reader.lines().collect(Collectors.joining());
            List<RssSourceDTO> sources = JsonUtils.fromJson(json, List.class);

            for (RssSourceDTO sourceDTO : sources) {
                if (!rssSourceRepository.existsByUrl(sourceDTO.getUrl())) {
                    RssSource source = new RssSource();
                    source.setName(sourceDTO.getName());
                    source.setUrl(sourceDTO.getUrl());
                    source.setCategory(sourceDTO.getCategory());
                    source.setDescription(sourceDTO.getDescription());
                    source.setUpdateInterval(sourceDTO.getUpdateInterval());
                    rssSourceRepository.save(source);
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("导入失败: " + e.getMessage());
        }
    }

    public byte[] exportSources() {
        try {
            List<RssSource> sources = rssSourceRepository.findAll();
            List<RssSourceDTO> sourceDTOs = sources.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return JsonUtils.toJsonBytes(sourceDTOs);
        } catch (Exception e) {
            throw new BadRequestException("导出失败: " + e.getMessage());
        }
    }

    private RssSourceDTO convertToDTO(RssSource source) {
        RssSourceDTO dto = new RssSourceDTO();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setUrl(source.getUrl());
        dto.setCategory(source.getCategory());
        dto.setDescription(source.getDescription());
        dto.setUpdateInterval(source.getUpdateInterval());
        dto.setStatus(source.getStatus());
        dto.setLastUpdate(source.getLastUpdate());
        dto.setLastError(source.getLastError());
        dto.setCreatedAt(source.getCreatedAt());
        dto.setUpdatedAt(source.getUpdatedAt());
        return dto;
    }

    private Map<String, Object> convertEntryToMap(SyndEntry entry) {
        return Map.of(
                "title", entry.getTitle(),
                "link", entry.getLink(),
                "description", entry.getDescription() != null ? entry.getDescription().getValue() : "",
                "pubDate", entry.getPublishedDate() != null ? entry.getPublishedDate() : LocalDateTime.now(),
                "guid", entry.getUri() != null ? entry.getUri() : entry.getLink()
        );
    }
} 