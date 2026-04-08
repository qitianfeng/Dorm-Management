package com.dormitory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 退宿请求DTO
 */
@Data
public class CheckOutRequest {
    
    @NotNull(message = "住宿记录ID不能为空")
    private Long accommodationId;
    
    @NotNull(message = "退宿日期不能为空")
    private LocalDate checkOutDate;
    
    private String reason;
}
