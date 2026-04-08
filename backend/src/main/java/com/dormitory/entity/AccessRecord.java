package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_access_record")
public class AccessRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("user_name")
    private String userName;
    
    @TableField("building_id")
    private Long buildingId;
    
    @TableField("building_name")
    private String buildingName;
    
    @TableField("access_type")
    private String accessType;
    
    @TableField("access_time")
    private LocalDateTime accessTime;
    
    @TableField("device_location")
    private String deviceLocation;
    
    @TableField("device_id")
    private String deviceId;
    
    private String result;
    
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}
