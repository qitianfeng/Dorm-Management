package com.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访客登记请求DTO
 */
@Data
public class VisitorRequest {
    
    @NotBlank(message = "访客姓名不能为空")
    private String visitorName;
    
    private String visitorPhone;
    
    private String idCard;
    
    private String visitorType;
    
    @NotNull(message = "被访房间ID不能为空")
    private Long visitRoomId;
    
    @NotNull(message = "被访学生ID不能为空")
    private Long visitStudentId;
    
    private String visitStudentName;
    
    @NotBlank(message = "来访事由不能为空")
    private String visitReason;
    
    @NotNull(message = "来访时间不能为空")
    private LocalDateTime visitTime;
    
    private String remark;
    
    private String register;
}
