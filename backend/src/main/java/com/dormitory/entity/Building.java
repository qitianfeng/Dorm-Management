package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_building")
public class Building {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("building_code")
    private String buildingCode;
    
    @TableField("building_name")
    private String buildingName;
    
    @TableField("building_type")
    private String buildingType;
    
    @TableField("gender_type")
    private String genderType;
    
    @TableField("total_floors")
    private Integer totalFloors;
    
    @TableField("total_rooms")
    private Integer totalRooms;
    
    @TableField("total_beds")
    private Integer totalBeds;
    
    @TableField("available_beds")
    private Integer availableBeds;
    
    private String location;
    
    @TableField("manager_name")
    private String managerName;
    
    @TableField("manager_phone")
    private String managerPhone;
    
    private String status;
    
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
