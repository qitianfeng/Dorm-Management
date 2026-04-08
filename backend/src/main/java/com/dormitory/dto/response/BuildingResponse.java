package com.dormitory.dto.response;

import com.dormitory.entity.Building;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 宿舍楼响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingResponse {
    
    private Long id;
    private String buildingCode;
    private String buildingName;
    private String buildingType;
    private Integer totalFloors;
    private Integer totalRooms;
    private Integer totalBeds;
    private Integer availableBeds;
    private String location;
    private String managerName;
    private String managerPhone;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
