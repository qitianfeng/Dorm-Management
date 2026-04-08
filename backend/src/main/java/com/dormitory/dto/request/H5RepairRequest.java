package com.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * H5端报修请求DTO
 */
@Data
public class H5RepairRequest {
    
    private Long studentId;
    
    @NotBlank(message = "房间号不能为空")
    private String roomNumber;
    
    @NotBlank(message = "报修类型不能为空")
    private String repairType;
    
    @NotBlank(message = "问题描述不能为空")
    private String description;
    
    private String images;
    
    private String priority = "NORMAL";
}
