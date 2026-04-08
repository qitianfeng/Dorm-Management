package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 换宿舍申请实体
 */
@Data
@TableName("dorm_room_transfer")
public class RoomTransfer {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("student_name")
    private String studentName;
    
    // 原宿舍信息
    @TableField("old_room_id")
    private Long oldRoomId;
    
    @TableField("old_room_number")
    private String oldRoomNumber;
    
    @TableField("old_building_id")
    private Long oldBuildingId;
    
    @TableField("old_building_name")
    private String oldBuildingName;
    
    @TableField("old_bed_number")
    private String oldBedNumber;
    
    // 新宿舍信息
    @TableField("new_room_id")
    private Long newRoomId;
    
    @TableField("new_room_number")
    private String newRoomNumber;
    
    @TableField("new_building_id")
    private Long newBuildingId;
    
    @TableField("new_building_name")
    private String newBuildingName;
    
    @TableField("new_bed_number")
    private String newBedNumber;
    
    // 申请信息
    @TableField("reason")
    private String reason;
    
    @TableField("status")
    private String status;
    
    @TableField("apply_time")
    private LocalDateTime applyTime;
    
    // 审批信息
    @TableField("approve_time")
    private LocalDateTime approveTime;
    
    @TableField("approver_id")
    private Long approverId;
    
    @TableField("approver_name")
    private String approverName;
    
    @TableField("reject_reason")
    private String rejectReason;
    
    @TableField("remark")
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
