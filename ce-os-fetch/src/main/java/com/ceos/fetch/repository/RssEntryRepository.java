package com.ceos.fetch.repository;

import com.ceos.fetch.entity.RssEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RssEntryRepository extends JpaRepository<RssEntry, String> {
    boolean existsByGuid(String guid);
} 