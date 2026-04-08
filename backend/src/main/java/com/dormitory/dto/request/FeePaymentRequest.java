package com.dormitory.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 缴费支付请求DTO
 */
@Data
public class FeePaymentRequest {
    
    private String status;
    
    private String paymentMethod;
    
    private String transactionId;
    
    private BigDecimal paidAmount;
}
