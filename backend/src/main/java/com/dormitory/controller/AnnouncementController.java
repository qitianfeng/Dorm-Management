package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.AnnouncementRequest;
import com.dormitory.dto.response.AnnouncementResponse;
import com.dormitory.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告通知控制器
 */
@Tag(name = "公告通知管理", description = "公告发布、查询等接口")
@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    
    @Operation(summary = "创建公告")
    @PostMapping
    public Result<AnnouncementResponse> create(@Valid @RequestBody AnnouncementRequest request,
                                                Authentication authentication) {
        Long publisherId = Long.parseLong(authentication.getName());
        AnnouncementResponse response = announcementService.create(request, publisherId);
        return Result.success("创建成功", response);
    }
    
    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    public Result<AnnouncementResponse> update(@PathVariable Long id, 
                                                @Valid @RequestBody AnnouncementRequest request) {
        AnnouncementResponse response = announcementService.update(id, request);
        return Result.success("更新成功", response);
    }
    
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public Result<AnnouncementResponse> getById(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.getById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "查看公告（增加浏览量）")
    @PostMapping("/{id}/view")
    public Result<AnnouncementResponse> view(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.incrementViewCount(id);
        return Result.success(response);
    }
    
    @Operation(summary = "获取已发布公告列表")
    @GetMapping("/published")
    public Result<List<AnnouncementResponse>> getPublished() {
        List<AnnouncementResponse> responses = announcementService.getPublished();
        return Result.success(responses);
    }
    
    @Operation(summary = "发布公告")
    @PutMapping("/{id}/publish")
    public Result<AnnouncementResponse> publish(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.publish(id);
        return Result.success("发布成功", response);
    }
    
    @Operation(summary = "分页查询公告")
    @GetMapping("/page")
    public Result<PageResult<AnnouncementResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        PageResult<AnnouncementResponse> result = announcementService.getPage(page, size, type, keyword);
        return Result.success(result);
    }
}
