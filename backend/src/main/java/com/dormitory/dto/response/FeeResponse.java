package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费记录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeResponse {
    
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private String feeType;
    private BigDecimal amount;
    private LocalDateTime dueTime;
    private LocalDateTime paidTime;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
