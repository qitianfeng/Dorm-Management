package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.VisitorRequest;
import com.dormitory.dto.response.VisitorResponse;
import com.dormitory.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 访客登记控制器
 */
@Tag(name = "访客登记管理", description = "访客登记和管理接口")
@RestController
@RequestMapping("/visitor")
@RequiredArgsConstructor
public class VisitorController {
    
    private final VisitorService visitorService;
    
    @Operation(summary = "登记访客")
    @PostMapping
    public Result<VisitorResponse> create(@Valid @RequestBody VisitorRequest request) {
        VisitorResponse response = visitorService.create(request);
        return Result.success("登记成功", response);
    }
    
    @Operation(summary = "更新访客信息")
    @PutMapping("/{id}")
    public Result<VisitorResponse> update(@PathVariable Long id, @Valid @RequestBody VisitorRequest request) {
        VisitorResponse response = visitorService.update(id, request);
        return Result.success("更新成功", response);
    }
    
    @Operation(summary = "访客离开")
    @PutMapping("/{id}/leave")
    public Result<VisitorResponse> leave(@PathVariable Long id) {
        VisitorResponse response = visitorService.leave(id);
        return Result.success("已登记离开", response);
    }
    
    @Operation(summary = "删除访客记录")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        visitorService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取访客详情")
    @GetMapping("/{id}")
    public Result<VisitorResponse> getById(@PathVariable Long id) {
        VisitorResponse response = visitorService.getById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "根据房间获取访客记录")
    @GetMapping("/room/{roomId}")
    public Result<List<VisitorResponse>> getByRoomId(@PathVariable Long roomId) {
        List<VisitorResponse> responses = visitorService.getByRoomId(roomId);
        return Result.success(responses);
    }
    
    @Operation(summary = "获取访问中的访客列表")
    @GetMapping("/visiting")
    public Result<List<VisitorResponse>> getVisiting() {
        List<VisitorResponse> responses = visitorService.getVisiting();
        return Result.success(responses);
    }
    
    @Operation(summary = "分页查询访客记录")
    @GetMapping("/page")
    public Result<PageResult<VisitorResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        PageResult<VisitorResponse> result = visitorService.getPage(page, size, roomId, status, keyword);
        return Result.success(result);
    }
}
