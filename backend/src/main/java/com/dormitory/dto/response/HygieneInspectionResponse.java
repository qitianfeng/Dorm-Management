package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 卫生检查响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HygieneInspectionResponse {
    
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String buildingName;
    private LocalDate inspectionDate;
    private Integer score;
    private String grade;
    private String items;
    private String problems;
    private String images;
    private Long inspectorId;
    private String inspectorName;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
