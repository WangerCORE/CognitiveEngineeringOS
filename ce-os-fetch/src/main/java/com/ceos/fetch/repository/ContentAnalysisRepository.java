package com.ceos.fetch.repository;

import com.ceos.fetch.entity.ContentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentAnalysisRepository extends JpaRepository<ContentAnalysis, String> {
    Optional<ContentAnalysis> findByEntryId(String entryId);
    boolean existsByEntryId(String entryId);
} 