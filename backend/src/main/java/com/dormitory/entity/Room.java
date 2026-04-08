package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_room")
public class Room {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("building_id")
    private Long buildingId;
    
    @TableField("room_number")
    private String roomNumber;
    
    private Integer floor;
    
    private Integer capacity;
    
    @TableField("occupied_beds")
    private Integer occupiedBeds;
    
    @TableField("room_type")
    private String roomType;
    
    private String status;
    
    private String facilities;
    
    @TableField("monthly_fee")
    private Double monthlyFee;
    
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
