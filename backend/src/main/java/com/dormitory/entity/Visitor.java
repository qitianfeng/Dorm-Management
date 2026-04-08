package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_visitor")
public class Visitor {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("visitor_name")
    private String visitorName;
    
    @TableField("visitor_phone")
    private String visitorPhone;
    
    @TableField("id_card")
    private String idCard;
    
    @TableField("visitor_type")
    private String visitorType;
    
    @TableField("visit_room_id")
    private Long visitRoomId;
    
    @TableField("visit_student_id")
    private Long visitStudentId;
    
    @TableField("visit_student_name")
    private String visitStudentName;
    
    @TableField("visit_reason")
    private String visitReason;
    
    @TableField("visit_time")
    private LocalDateTime visitTime;
    
    @TableField("leave_time")
    private LocalDateTime leaveTime;
    
    private String status;
    
    private String remark;
    
    private String register;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
