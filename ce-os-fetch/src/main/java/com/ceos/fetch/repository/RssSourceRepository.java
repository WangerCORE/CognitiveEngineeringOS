package com.ceos.fetch.repository;

import com.ceos.fetch.entity.RssSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RssSourceRepository extends JpaRepository<RssSource, String> {
    List<RssSource> findByActiveTrue();
    boolean existsByUrl(String url);
} 