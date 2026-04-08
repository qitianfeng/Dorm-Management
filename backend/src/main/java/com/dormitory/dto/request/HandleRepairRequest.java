package com.dormitory.dto.request;

import lombok.Data;

/**
 * 处理报修请求DTO
 */
@Data
public class HandleRepairRequest {
    
    private Long handlerId;
    
    private String handleResult;
    
    private Double repairCost;
    
    private String status;
}
