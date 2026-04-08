package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生住宿记录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationResponse {
    
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private Long roomId;
    private String roomNumber;
    private Long buildingId;
    private String buildingName;
    private String bedNumber;
    private LocalDate checkInDate;
    private LocalDate expectedCheckOutDate;
    private LocalDate actualCheckOutDate;
    private String status;
    private BigDecimal deposit;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
