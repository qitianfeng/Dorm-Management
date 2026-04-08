package com.dormitory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 违纪记录响应DTO
 */
@Data
@Builder
public class DisciplinaryResponse {
    
    private Long id;
    
    private Long studentId;
    
    private String studentName;
    
    private String studentIdNumber;
    
    private Long roomId;
    
    private String buildingName;
    
    private String roomNumber;
    
    private String violationType;
    
    private String description;
    
    private LocalDateTime violationTime;
    
    private String punishment;
    
    private Double fine;
    
    private String status;
    
    private String processNote;
    
    private Long recorderId;
    
    private String recorderName;
    
    private String remark;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
