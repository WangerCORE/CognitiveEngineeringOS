package com.example.ceos.repository;

import com.example.ceos.entity.RssSource;
import com.example.ceos.entity.SourceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RssSourceRepository extends JpaRepository<RssSource, Long> {
    boolean existsByUrl(String url);

    @Query("SELECT s FROM RssSource s WHERE " +
           "(:query IS NULL OR s.name LIKE %:query% OR s.url LIKE %:query%) AND " +
           "(:status IS NULL OR s.status = :status)")
    Page<RssSource> findBySearchCriteria(
            @Param("query") String query,
            @Param("status") SourceStatus status,
            Pageable pageable);

    List<RssSource> findByStatusAndUpdateIntervalLessThanEqual(
            SourceStatus status,
            Integer updateInterval);

    @Query("SELECT s FROM RssSource s WHERE s.user.id = :userId")
    Page<RssSource> findByUserId(@Param("userId") Long userId, Pageable pageable);
} 