package com.example.ceos.repository;

import com.example.ceos.entity.RssEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RssEntryRepository extends JpaRepository<RssEntry, Long> {
    boolean existsByGuid(String guid);

    @Query("SELECT e FROM RssEntry e WHERE " +
           "e.user.id = :userId AND " +
           "(:query IS NULL OR e.title LIKE %:query% OR e.description LIKE %:query%) AND " +
           "(:sourceId IS NULL OR e.source.id = :sourceId) AND " +
           "(:isRead IS NULL OR e.isRead = :isRead) AND " +
           "(:isStarred IS NULL OR e.isStarred = :isStarred)")
    Page<RssEntry> findBySearchCriteria(
            @Param("userId") Long userId,
            @Param("query") String query,
            @Param("sourceId") Long sourceId,
            @Param("isRead") Boolean isRead,
            @Param("isStarred") Boolean isStarred,
            Pageable pageable);

    @Query("SELECT e FROM RssEntry e WHERE e.source.id = :sourceId AND e.guid IN :guids")
    List<RssEntry> findBySourceIdAndGuids(
            @Param("sourceId") Long sourceId,
            @Param("guids") List<String> guids);
} 