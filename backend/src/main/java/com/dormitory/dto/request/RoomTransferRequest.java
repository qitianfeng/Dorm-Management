package com.dormitory.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 换宿舍申请请求
 */
@Data
public class RoomTransferRequest {
    
    @NotNull(message = "新房间ID不能为空")
    private Long newRoomId;
    
    @NotBlank(message = "新床位号不能为空")
    private String newBedNumber;
    
    @NotBlank(message = "换宿原因不能为空")
    private String reason;
}
