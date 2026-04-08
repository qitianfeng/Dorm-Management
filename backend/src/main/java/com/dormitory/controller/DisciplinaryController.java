package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.DisciplinaryRequest;
import com.dormitory.dto.response.DisciplinaryResponse;
import com.dormitory.service.DisciplinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 违纪记录控制器
 */
@Tag(name = "违纪记录管理", description = "违纪记录管理接口")
@RestController
@RequestMapping("/disciplinary")
@RequiredArgsConstructor
public class DisciplinaryController {
    
    private final DisciplinaryService disciplinaryService;
    
    @Operation(summary = "创建违纪记录")
    @PostMapping
    public Result<DisciplinaryResponse> create(@Valid @RequestBody DisciplinaryRequest request) {
        DisciplinaryResponse response = disciplinaryService.create(request);
        return Result.success("创建成功", response);
    }
    
    @Operation(summary = "更新违纪记录")
    @PutMapping("/{id}")
    public Result<DisciplinaryResponse> update(@PathVariable Long id, @Valid @RequestBody DisciplinaryRequest request) {
        DisciplinaryResponse response = disciplinaryService.update(id, request);
        return Result.success("更新成功", response);
    }
    
    @Operation(summary = "删除违纪记录")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        disciplinaryService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取违纪记录详情")
    @GetMapping("/{id}")
    public Result<DisciplinaryResponse> getById(@PathVariable Long id) {
        DisciplinaryResponse response = disciplinaryService.getById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "分页查询违纪记录")
    @GetMapping("/page")
    public Result<PageResult<DisciplinaryResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        PageResult<DisciplinaryResponse> result = disciplinaryService.getPage(page, size, keyword, type, status);
        return Result.success(result);
    }
}
