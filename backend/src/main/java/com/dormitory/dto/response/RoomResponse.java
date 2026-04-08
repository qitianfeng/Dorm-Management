package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    
    private Long id;
    private Long buildingId;
    private String buildingName;
    private String roomNumber;
    private Integer floor;
    private Integer capacity;
    private Integer occupiedBeds;
    private Integer availableBeds;
    private String roomType;
    private String status;
    private String facilities;
    private BigDecimal monthlyFee;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
