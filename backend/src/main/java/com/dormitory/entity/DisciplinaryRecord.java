package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_disciplinary")
public class DisciplinaryRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("student_name")
    private String studentName;
    
    @TableField("room_id")
    private Long roomId;
    
    @TableField("violation_type")
    private String violationType;
    
    private String description;
    
    @TableField("violation_time")
    private LocalDateTime violationTime;
    
    private String punishment;
    
    private Double fine;
    
    private String status;
    
    @TableField("recorder_id")
    private Long recorderId;
    
    @TableField("recorder_name")
    private String recorderName;
    
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
