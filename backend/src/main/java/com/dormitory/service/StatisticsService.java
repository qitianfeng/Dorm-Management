package com.dormitory.service;

import com.dormitory.dto.response.StatisticsResponse;

import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取总体统计
     */
    StatisticsResponse getOverview();
    
    /**
     * 获取入住率趋势（最近7天）
     */
    Map<String, Object> getOccupancyTrend();
    
    /**
     * 获取报修类型分布
     */
    Map<String, Object> getRepairTypeDistribution();
    
    /**
     * 获取卫生评分趋势
     */
    Map<String, Object> getHygieneScoreTrend();
    
    /**
     * 获取楼栋入住情况
     */
    Map<String, Object> getBuildingOccupancy();
}
