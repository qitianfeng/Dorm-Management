package com.dormitory.dto.request;

import com.dormitory.entity.HygieneInspection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 卫生检查请求DTO
 */
@Data
public class HygieneInspectionRequest {
    
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
    
    @NotNull(message = "检查日期不能为空")
    private LocalDate inspectionDate;
    
    @NotNull(message = "评分不能为空")
    private Integer score;
    
    private String items;
    
    private String problems;
    
    private String images;
    
    @NotNull(message = "检查人ID不能为空")
    private Long inspectorId;
    
    private String inspectorName;
    
    private String remark;
}
