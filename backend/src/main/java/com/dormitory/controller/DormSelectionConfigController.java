package com.dormitory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.DormSelectionConfig;
import com.dormitory.mapper.DormSelectionConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 选宿舍配置管理控制器（PC管理端）
 */
@Tag(name = "选宿舍配置管理", description = "管理员配置选宿舍参数")
@RestController
@RequestMapping("/dorm-selection/config")
@RequiredArgsConstructor
public class DormSelectionConfigController {
    
    private final DormSelectionConfigMapper configMapper;
    
    @Operation(summary = "获取所有选宿舍配置")
    @GetMapping("/list")
    public Result<List<DormSelectionConfig>> list() {
        List<DormSelectionConfig> configs = configMapper.selectList(
            new LambdaQueryWrapper<DormSelectionConfig>()
                .orderByDesc(DormSelectionConfig::getCreateTime)
        );
        return Result.success(configs);
    }
    
    @Operation(summary = "获取选宿舍配置详情")
    @GetMapping("/{id}")
    public Result<DormSelectionConfig> getById(@PathVariable Long id) {
        DormSelectionConfig config = configMapper.selectById(id);
        if (config == null) {
            return Result.error("配置不存在");
        }
        return Result.success(config);
    }
    
    @Operation(summary = "创建选宿舍配置")
    @PostMapping
    public Result<DormSelectionConfig> create(@RequestBody DormSelectionConfig config) {
        // 验证时间
        if (config.getStartTime().isAfter(config.getEndTime())) {
            return Result.error("开始时间不能晚于结束时间");
        }
        
        config.setStatus("INACTIVE");
        configMapper.insert(config);
        return Result.success("创建成功", config);
    }
    
    @Operation(summary = "更新选宿舍配置")
    @PutMapping("/{id}")
    public Result<DormSelectionConfig> update(@PathVariable Long id, @RequestBody DormSelectionConfig config) {
        DormSelectionConfig existing = configMapper.selectById(id);
        if (existing == null) {
            return Result.error("配置不存在");
        }
        
        // 验证时间
        if (config.getStartTime().isAfter(config.getEndTime())) {
            return Result.error("开始时间不能晚于结束时间");
        }
        
        config.setId(id);
        configMapper.updateById(config);
        return Result.success("更新成功", config);
    }
    
    @Operation(summary = "删除选宿舍配置")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        configMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "启用选宿舍配置")
    @PutMapping("/{id}/activate")
    public Result<?> activate(@PathVariable Long id) {
        DormSelectionConfig config = configMapper.selectById(id);
        if (config == null) {
            return Result.error("配置不存在");
        }
        
        config.setStatus("ACTIVE");
        configMapper.updateById(config);
        return Result.success("启用成功");
    }
    
    @Operation(summary = "停用选宿舍配置")
    @PutMapping("/{id}/deactivate")
    public Result<?> deactivate(@PathVariable Long id) {
        DormSelectionConfig config = configMapper.selectById(id);
        if (config == null) {
            return Result.error("配置不存在");
        }
        
        config.setStatus("INACTIVE");
        configMapper.updateById(config);
        return Result.success("停用成功");
    }
}
