package com.dormitory.dto.request;

import com.dormitory.entity.StudentAccommodation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入住请求DTO
 */
@Data
public class CheckInRequest {
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
    
    private String bedNumber;
    
    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;
    
    private LocalDate expectedCheckOutDate;
    
    private BigDecimal deposit;
    
    private String remark;
}
