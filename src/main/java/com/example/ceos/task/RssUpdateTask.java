package com.example.ceos.task;

import com.example.ceos.entity.RssEntry;
import com.example.ceos.entity.RssSource;
import com.example.ceos.entity.SourceStatus;
import com.example.ceos.entity.User;
import com.example.ceos.repository.RssEntryRepository;
import com.example.ceos.repository.RssSourceRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssUpdateTask {
    private final RssSourceRepository rssSourceRepository;
    private final RssEntryRepository rssEntryRepository;

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    @Transactional
    public void updateSources() {
        log.info("开始更新RSS源...");
        LocalDateTime now = LocalDateTime.now();

        List<RssSource> sources = rssSourceRepository.findByStatusAndUpdateIntervalLessThanEqual(
                SourceStatus.ACTIVE,
                (int) (now.getMinute() % 60));

        for (RssSource source : sources) {
            try {
                URL feedUrl = new URL(source.getUrl());
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));

                List<String> guids = feed.getEntries().stream()
                        .map(SyndEntry::getUri)
                        .collect(Collectors.toList());

                List<RssEntry> existingEntries = rssEntryRepository.findBySourceIdAndGuids(
                        source.getId(), guids);

                List<String> existingGuids = existingEntries.stream()
                        .map(RssEntry::getGuid)
                        .collect(Collectors.toList());

                List<RssEntry> newEntries = feed.getEntries().stream()
                        .filter(entry -> !existingGuids.contains(entry.getUri()))
                        .map(entry -> convertToRssEntry(entry, source))
                        .collect(Collectors.toList());

                if (!newEntries.isEmpty()) {
                    rssEntryRepository.saveAll(newEntries);
                    log.info("成功保存{}条新RSS条目: {}", newEntries.size(), source.getName());
                }

                source.setStatus(SourceStatus.ACTIVE);
                source.setLastUpdate(now);
                source.setLastError(null);
            } catch (Exception e) {
                log.error("更新RSS源失败: {}", source.getName(), e);
                source.setStatus(SourceStatus.ERROR);
                source.setLastError(e.getMessage());
            }
            rssSourceRepository.save(source);
        }

        log.info("RSS源更新完成");
    }

    private RssEntry convertToRssEntry(SyndEntry entry, RssSource source) {
        RssEntry rssEntry = new RssEntry();
        rssEntry.setTitle(entry.getTitle());
        rssEntry.setDescription(entry.getDescription() != null ? entry.getDescription().getValue() : "");
        rssEntry.setLink(entry.getLink());
        rssEntry.setGuid(entry.getUri() != null ? entry.getUri() : entry.getLink());
        rssEntry.setPubDate(entry.getPublishedDate() != null ? entry.getPublishedDate().toLocalDateTime() : LocalDateTime.now());
        rssEntry.setAuthor(entry.getAuthor());
        rssEntry.setContent(entry.getContents() != null && !entry.getContents().isEmpty() ?
                entry.getContents().get(0).getValue() : "");
        rssEntry.setSource(source);
        rssEntry.setUser(source.getUser());
        return rssEntry;
    }
} 