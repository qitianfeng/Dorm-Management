package com.dormitory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 换宿舍申请响应
 */
@Data
@Builder
public class RoomTransferResponse {
    
    private Long id;
    
    // 学生信息
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private String department;
    private String className;
    
    // 原宿舍信息
    private Long oldRoomId;
    private String oldRoomNumber;
    private Long oldBuildingId;
    private String oldBuildingName;
    private String oldBedNumber;
    
    // 新宿舍信息
    private Long newRoomId;
    private String newRoomNumber;
    private Long newBuildingId;
    private String newBuildingName;
    private String newBedNumber;
    
    // 申请信息
    private String reason;
    private String status;
    private LocalDateTime applyTime;
    
    // 审批信息
    private LocalDateTime approveTime;
    private Long approverId;
    private String approverName;
    private String rejectReason;
    
    private String remark;
    private LocalDateTime createTime;
}
