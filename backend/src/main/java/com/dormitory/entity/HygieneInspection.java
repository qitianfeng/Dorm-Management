package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("dorm_hygiene")
public class HygieneInspection {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("room_id")
    private Long roomId;
    
    @TableField("building_id")
    private Long buildingId;
    
    @TableField("inspection_date")
    private LocalDate inspectionDate;
    
    private Integer score;
    
    private String grade;
    
    private String items;
    
    private String problems;
    
    private String images;
    
    @TableField("inspector_id")
    private Long inspectorId;
    
    @TableField("inspector_name")
    private String inspectorName;
    
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
