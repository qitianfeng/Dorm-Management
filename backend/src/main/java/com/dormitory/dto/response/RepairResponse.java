package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报修记录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairResponse {
    
    private Long id;
    private Long studentId;
    private String studentName;
    private Long roomId;
    private Long buildingId;
    private String roomNumber;
    private String buildingName;
    private String repairType;
    private String description;
    private String images;
    private String priority;
    private String status;
    private Long handlerId;
    private String handlerName;
    private String handleResult;
    private LocalDateTime handleTime;
    private Double repairCost;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
