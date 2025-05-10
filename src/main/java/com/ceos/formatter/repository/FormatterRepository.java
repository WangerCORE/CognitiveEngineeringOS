package com.ceos.formatter.repository;

import com.ceos.formatter.entity.Formatter;
import com.ceos.formatter.entity.FormatterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormatterRepository extends JpaRepository<Formatter, Long> {
    @Query("SELECT f FROM Formatter f WHERE " +
           "(:keyword IS NULL OR f.name LIKE %:keyword%) AND " +
           "(:status IS NULL OR f.status = :status)")
    Page<Formatter> findByKeywordAndStatus(
        @Param("keyword") String keyword,
        @Param("status") FormatterStatus status,
        Pageable pageable
    );

    @Query("SELECT COUNT(f) FROM Formatter f WHERE f.status = :status")
    long countByStatus(@Param("status") FormatterStatus status);

    @Query("SELECT SUM(f.usageCount) FROM Formatter f")
    long sumUsageCount();
} 