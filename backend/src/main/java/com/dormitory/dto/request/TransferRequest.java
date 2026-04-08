package com.dormitory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 调宿请求DTO
 */
@Data
public class TransferRequest {
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "原房间ID不能为空")
    private Long oldRoomId;
    
    @NotNull(message = "新房间ID不能为空")
    private Long newRoomId;
    
    private String newBedNumber;
    
    @NotNull(message = "调宿日期不能为空")
    private LocalDate transferDate;
    
    private String reason;
}
