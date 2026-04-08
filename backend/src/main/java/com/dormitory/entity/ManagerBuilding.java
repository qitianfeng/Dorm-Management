package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宿管楼栋关联实体
 */
@Data
@TableName("dorm_manager_building")
public class ManagerBuilding {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("building_id")
    private Long buildingId;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}
