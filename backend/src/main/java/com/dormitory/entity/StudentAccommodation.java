package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("dorm_accommodation")
public class StudentAccommodation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("room_id")
    private Long roomId;
    
    @TableField("building_id")
    private Long buildingId;
    
    @TableField("bed_number")
    private String bedNumber;
    
    @TableField("check_in_date")
    private LocalDate checkInDate;
    
    @TableField("expected_check_out_date")
    private LocalDate expectedCheckOutDate;
    
    @TableField("actual_check_out_date")
    private LocalDate actualCheckOutDate;
    
    private String status;
    
    private BigDecimal deposit;
    
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
