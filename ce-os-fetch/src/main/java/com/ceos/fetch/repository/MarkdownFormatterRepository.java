package com.ceos.fetch.repository;

import com.ceos.fetch.entity.MarkdownFormatter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkdownFormatterRepository extends JpaRepository<MarkdownFormatter, String> {
    List<MarkdownFormatter> findByActiveTrue();
    boolean existsByName(String name);
} 