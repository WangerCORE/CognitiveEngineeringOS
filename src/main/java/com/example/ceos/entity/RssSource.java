package com.example.ceos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rss_sources")
public class RssSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "名称不能为空")
    @Size(min = 2, max = 50, message = "名称长度必须在2-50个字符之间")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "URL不能为空")
    @Column(nullable = false, unique = true)
    private String url;

    @NotBlank(message = "分类不能为空")
    @Column(nullable = false)
    private String category;

    @Column(length = 500)
    private String description;

    @NotNull(message = "更新间隔不能为空")
    @Min(value = 5, message = "更新间隔最小为5分钟")
    @Column(name = "update_interval", nullable = false)
    private Integer updateInterval;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SourceStatus status = SourceStatus.ACTIVE;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "last_error")
    private String lastError;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
} 