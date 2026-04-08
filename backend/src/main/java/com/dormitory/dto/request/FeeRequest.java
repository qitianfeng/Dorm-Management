package com.dormitory.dto.request;

import com.dormitory.entity.Fee;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费请求DTO
 */
@Data
public class FeeRequest {
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "费用类型不能为空")
    private String feeType;
    
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    
    @NotNull(message = "应缴时间不能为空")
    private LocalDateTime dueTime;
    
    private String remark;
    
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;
}
