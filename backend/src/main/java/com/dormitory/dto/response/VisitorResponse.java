package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 访客登记响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorResponse {
    
    private Long id;
    private String visitorName;
    private String visitorPhone;
    private String idCard;
    private String visitorType;
    private Long visitRoomId;
    private String roomNumber;
    private String buildingName;
    private Long visitStudentId;
    private String visitStudentName;
    private String visitReason;
    private LocalDateTime visitTime;
    private LocalDateTime leaveTime;
    private String status;
    private String remark;
    private String register;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
