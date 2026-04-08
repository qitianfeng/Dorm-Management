package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.dto.response.StatisticsResponse;
import com.dormitory.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 统计报表控制器
 */
@Tag(name = "统计报表", description = "数据统计和分析接口")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @Operation(summary = "获取总体统计")
    @GetMapping("/overview")
    public Result<StatisticsResponse> getOverview() {
        StatisticsResponse response = statisticsService.getOverview();
        return Result.success(response);
    }
    
    @Operation(summary = "获取入住率趋势")
    @GetMapping("/occupancy-trend")
    public Result<Map<String, Object>> getOccupancyTrend() {
        Map<String, Object> result = statisticsService.getOccupancyTrend();
        return Result.success(result);
    }
    
    @Operation(summary = "获取报修类型分布")
    @GetMapping("/repair-distribution")
    public Result<Map<String, Object>> getRepairTypeDistribution() {
        Map<String, Object> result = statisticsService.getRepairTypeDistribution();
        return Result.success(result);
    }
    
    @Operation(summary = "获取卫生评分趋势")
    @GetMapping("/hygiene-trend")
    public Result<Map<String, Object>> getHygieneScoreTrend() {
        Map<String, Object> result = statisticsService.getHygieneScoreTrend();
        return Result.success(result);
    }
    
    @Operation(summary = "获取楼栋入住情况")
    @GetMapping("/building-occupancy")
    public Result<Map<String, Object>> getBuildingOccupancy() {
        Map<String, Object> result = statisticsService.getBuildingOccupancy();
        return Result.success(result);
    }
}
