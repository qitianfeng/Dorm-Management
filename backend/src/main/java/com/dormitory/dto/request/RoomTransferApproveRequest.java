package com.dormitory.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 换宿舍审批请求
 */
@Data
public class RoomTransferApproveRequest {
    
    @NotNull(message = "申请ID不能为空")
    private Long id;
    
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;
    
    private String rejectReason;
}
