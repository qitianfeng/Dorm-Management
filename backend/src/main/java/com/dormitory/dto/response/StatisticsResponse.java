package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计数据响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    
    // 宿舍统计
    private Long totalBuildings;
    private Long totalRooms;
    private Long totalBeds;
    private Long availableBeds;
    private Double occupancyRate;
    
    // 学生统计
    private Long totalStudents;
    private Long checkedInStudents;
    
    // 报修统计
    private Long totalRepairs;
    private Long pendingRepairs;
    private Long processingRepairs;
    private Long completedRepairs;
    
    // 卫生统计
    private Long totalInspections;
    private Long excellentCount;
    private Long goodCount;
    private Long qualifiedCount;
    private Long unqualifiedCount;
    
    // 缴费统计
    private Long totalFees;
    private Long paidFees;
    private Long unpaidFees;
    private Double totalAmount;
    private Double paidAmount;
    private Double unpaidAmount;
    
    // 访客统计
    private Long totalVisitors;
    private Long visitingCount;
    
    // 违纪统计
    private Long totalViolations;
    
    // 今日统计
    private Long todayCheckIns;
    private Long todayCheckOuts;
    private Long todayRepairs;
    private Long todayVisitors;
}
