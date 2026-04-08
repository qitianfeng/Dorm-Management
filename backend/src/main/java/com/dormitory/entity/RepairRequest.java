package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_repair")
public class RepairRequest {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("room_id")
    private Long roomId;
    
    @TableField("repair_type")
    private String repairType;
    
    private String description;
    
    private String images;
    
    private String priority;
    
    private String status;
    
    @TableField("handler_id")
    private Long handlerId;
    
    @TableField("handle_result")
    private String handleResult;
    
    @TableField("handle_time")
    private LocalDateTime handleTime;
    
    @TableField("repair_cost")
    private Double repairCost;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
