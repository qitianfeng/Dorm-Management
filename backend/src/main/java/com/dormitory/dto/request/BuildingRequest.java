package com.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 宿舍楼请求DTO
 */
@Data
public class BuildingRequest {
    
    @NotBlank(message = "楼栋编号不能为空")
    private String buildingCode;
    
    @NotBlank(message = "楼栋名称不能为空")
    private String buildingName;
    
    private String buildingType;
    
    private Integer totalFloors;
    
    private Integer totalRooms;
    
    private Integer totalBeds;
    
    private String location;
    
    private String managerName;
    
    private String managerPhone;
    
    private String status;
    
    private String remark;
}
