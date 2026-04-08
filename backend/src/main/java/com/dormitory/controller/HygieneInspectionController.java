package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.HygieneInspectionRequest;
import com.dormitory.dto.response.HygieneInspectionResponse;
import com.dormitory.service.HygieneInspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 卫生检查控制器
 */
@Tag(name = "卫生检查管理", description = "卫生检查记录管理接口")
@RestController
@RequestMapping("/hygiene")
@RequiredArgsConstructor
public class HygieneInspectionController {
    
    private final HygieneInspectionService hygieneService;
    
    @Operation(summary = "创建检查记录")
    @PostMapping
    public Result<HygieneInspectionResponse> create(@Valid @RequestBody HygieneInspectionRequest request) {
        HygieneInspectionResponse response = hygieneService.create(request);
        return Result.success("创建成功", response);
    }
    
    @Operation(summary = "更新检查记录")
    @PutMapping("/{id}")
    public Result<HygieneInspectionResponse> update(@PathVariable Long id, @Valid @RequestBody HygieneInspectionRequest request) {
        HygieneInspectionResponse response = hygieneService.update(id, request);
        return Result.success("更新成功", response);
    }
    
    @Operation(summary = "删除检查记录")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        hygieneService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取检查记录详情")
    @GetMapping("/{id}")
    public Result<HygieneInspectionResponse> getById(@PathVariable Long id) {
        HygieneInspectionResponse response = hygieneService.getById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "根据房间ID获取检查记录")
    @GetMapping("/room/{roomId}")
    public Result<List<HygieneInspectionResponse>> getByRoomId(@PathVariable Long roomId) {
        List<HygieneInspectionResponse> responses = hygieneService.getByRoomId(roomId);
        return Result.success(responses);
    }
    
    @Operation(summary = "根据楼栋ID获取检查记录")
    @GetMapping("/building/{buildingId}")
    public Result<List<HygieneInspectionResponse>> getByBuildingId(@PathVariable Long buildingId) {
        List<HygieneInspectionResponse> responses = hygieneService.getByBuildingId(buildingId);
        return Result.success(responses);
    }
    
    @Operation(summary = "分页查询检查记录")
    @GetMapping("/page")
    public Result<PageResult<HygieneInspectionResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String grade) {
        PageResult<HygieneInspectionResponse> result = hygieneService.getPage(page, size, buildingId, roomId, grade);
        return Result.success(result);
    }
}
