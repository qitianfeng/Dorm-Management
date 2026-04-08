package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.HandleRepairRequest;
import com.dormitory.dto.request.RepairRequestDto;
import com.dormitory.dto.response.RepairResponse;
import com.dormitory.service.RepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报修管理控制器
 */
@Tag(name = "报修管理", description = "报修申请、处理等接口")
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
public class RepairController {
    
    private final RepairService repairService;
    
    @Operation(summary = "提交报修申请")
    @PostMapping
    public Result<RepairResponse> create(@Valid @RequestBody RepairRequestDto request) {
        RepairResponse response = repairService.create(request);
        return Result.success("提交成功", response);
    }
    
    @Operation(summary = "处理报修")
    @PutMapping("/{id}/handle")
    public Result<RepairResponse> handle(@PathVariable Long id, @RequestBody HandleRepairRequest request) {
        RepairResponse response = repairService.handle(id, request);
        return Result.success("处理成功", response);
    }
    
    @Operation(summary = "取消报修")
    @PutMapping("/{id}/cancel")
    public Result<RepairResponse> cancel(@PathVariable Long id) {
        RepairResponse response = repairService.cancel(id);
        return Result.success("取消成功", response);
    }
    
    @Operation(summary = "获取报修详情")
    @GetMapping("/{id}")
    public Result<RepairResponse> getById(@PathVariable Long id) {
        RepairResponse response = repairService.getById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "根据学生ID获取报修列表")
    @GetMapping("/student/{studentId}")
    public Result<List<RepairResponse>> getByStudentId(@PathVariable Long studentId) {
        List<RepairResponse> responses = repairService.getByStudentId(studentId);
        return Result.success(responses);
    }
    
    @Operation(summary = "根据状态获取报修列表")
    @GetMapping("/status/{status}")
    public Result<List<RepairResponse>> getByStatus(@PathVariable String status) {
        List<RepairResponse> responses = repairService.getByStatus(status);
        return Result.success(responses);
    }
    
    @Operation(summary = "分页查询报修记录")
    @GetMapping("/page")
    public Result<PageResult<RepairResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        PageResult<RepairResponse> result = repairService.getPage(page, size, buildingId, status, keyword);
        return Result.success(result);
    }
}
