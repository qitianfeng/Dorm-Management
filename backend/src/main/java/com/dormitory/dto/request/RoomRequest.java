package com.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房间请求DTO
 */
@Data
public class RoomRequest {
    
    @NotNull(message = "楼栋ID不能为空")
    private Long buildingId;
    
    @NotBlank(message = "房间号不能为空")
    private String roomNumber;
    
    private Integer floor;
    
    private Integer capacity;
    
    private String roomType;
    
    private String status;
    
    private String facilities;
    
    private BigDecimal monthlyFee;
    
    private String remark;
}
