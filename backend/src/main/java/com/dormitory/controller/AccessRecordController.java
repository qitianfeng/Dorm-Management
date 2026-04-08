package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.AccessRecord;
import com.dormitory.service.AccessRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 门禁记录控制器
 */
@Tag(name = "门禁记录管理", description = "门禁记录查询接口")
@RestController
@RequestMapping("/access-record")
@RequiredArgsConstructor
public class AccessRecordController {
    
    private final AccessRecordService accessRecordService;
    
    @Operation(summary = "分页查询门禁记录")
    @GetMapping("/page")
    public Result<PageResult<AccessRecord>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String accessType,
            @RequestParam(required = false) String keyword) {
        PageResult<AccessRecord> result = accessRecordService.getPage(page, size, accessType, keyword);
        return Result.success(result);
    }
    
    @Operation(summary = "获取门禁记录详情")
    @GetMapping("/{id}")
    public Result<AccessRecord> getById(@PathVariable Long id) {
        AccessRecord record = accessRecordService.getById(id);
        return Result.success(record);
    }
    
    @Operation(summary = "删除门禁记录")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        accessRecordService.delete(id);
        return Result.success("删除成功");
    }
}
