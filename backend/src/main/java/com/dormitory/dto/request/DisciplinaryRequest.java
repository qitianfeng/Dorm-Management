package com.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 违纪记录请求DTO
 */
@Data
public class DisciplinaryRequest {
    
    private Long id;
    
    @NotBlank(message = "学号不能为空")
    private String studentId;
    
    @NotBlank(message = "违纪类型不能为空")
    private String violationType;
    
    @NotBlank(message = "违纪时间不能为空")
    private String violationTime;
    
    @NotBlank(message = "违纪描述不能为空")
    private String description;
    
    private String status;
    
    private String processNote;
}
