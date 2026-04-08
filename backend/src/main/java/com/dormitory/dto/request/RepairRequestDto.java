package com.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报修请求DTO
 */
@Data
public class RepairRequestDto {
    
    @NotNull(message = "申请人ID不能为空")
    private Long studentId;
    
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
    
    @NotBlank(message = "报修类型不能为空")
    private String repairType;
    
    @NotBlank(message = "问题描述不能为空")
    private String description;
    
    private String images;
    
    private String priority;
}
