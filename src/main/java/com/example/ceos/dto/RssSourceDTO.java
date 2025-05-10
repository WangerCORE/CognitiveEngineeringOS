package com.example.ceos.dto;

import com.example.ceos.entity.SourceStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RssSourceDTO {
    private Long id;

    @NotBlank(message = "名称不能为空")
    @Size(min = 2, max = 50, message = "名称长度必须在2-50个字符之间")
    private String name;

    @NotBlank(message = "URL不能为空")
    private String url;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String description;

    @NotNull(message = "更新间隔不能为空")
    @Min(value = 5, message = "更新间隔最小为5分钟")
    private Integer updateInterval;

    private SourceStatus status;
    private LocalDateTime lastUpdate;
    private String lastError;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 