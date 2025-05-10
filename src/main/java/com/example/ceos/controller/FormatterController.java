package com.example.ceos.controller;

import com.example.ceos.dto.FormatterDTO;
import com.example.ceos.dto.FormatterTestRequest;
import com.example.ceos.dto.FormatterTestResponse;
import com.example.ceos.service.FormatterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/formatters")
@Tag(name = "格式化器管理", description = "格式化器的CRUD和测试接口")
public class FormatterController {

    private final FormatterService formatterService;

    public FormatterController(FormatterService formatterService) {
        this.formatterService = formatterService;
    }

    @GetMapping
    @Operation(summary = "获取格式化器列表", description = "分页获取格式化器列表，支持按名称搜索和状态筛选")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取格式化器列表",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Page<FormatterDTO>> listFormatters(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(formatterService.listFormatters(keyword, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取格式化器详情", description = "根据ID获取格式化器详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取格式化器详情",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = FormatterDTO.class))),
        @ApiResponse(responseCode = "404", description = "格式化器不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<FormatterDTO> getFormatter(
            @Parameter(description = "格式化器ID") @PathVariable Long id) {
        return ResponseEntity.ok(formatterService.getFormatter(id));
    }

    @PostMapping
    @Operation(summary = "创建格式化器", description = "创建新的格式化器")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "成功创建格式化器",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = FormatterDTO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<FormatterDTO> createFormatter(
            @Parameter(description = "格式化器信息") @RequestBody FormatterDTO formatterDTO) {
        return ResponseEntity.ok(formatterService.createFormatter(formatterDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新格式化器", description = "更新指定ID的格式化器信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新格式化器",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = FormatterDTO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "格式化器不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<FormatterDTO> updateFormatter(
            @Parameter(description = "格式化器ID") @PathVariable Long id,
            @Parameter(description = "格式化器信息") @RequestBody FormatterDTO formatterDTO) {
        return ResponseEntity.ok(formatterService.updateFormatter(id, formatterDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除格式化器", description = "删除指定ID的格式化器")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "成功删除格式化器"),
        @ApiResponse(responseCode = "404", description = "格式化器不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteFormatter(
            @Parameter(description = "格式化器ID") @PathVariable Long id) {
        formatterService.deleteFormatter(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/test")
    @Operation(summary = "测试格式化器", description = "使用指定的格式化器测试格式化效果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功测试格式化器",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = FormatterTestResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "格式化器不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<FormatterTestResponse> testFormatter(
            @Parameter(description = "格式化器ID") @PathVariable Long id,
            @Parameter(description = "测试请求") @RequestBody FormatterTestRequest request) {
        return ResponseEntity.ok(formatterService.testFormatter(id, request));
    }
} 