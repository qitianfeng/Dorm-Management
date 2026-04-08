package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选宿舍记录实体
 */
@Data
@TableName("dorm_selection_record")
public class DormSelectionRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("config_id")
    private Long configId;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("student_name")
    private String studentName;
    
    @TableField("building_id")
    private Long buildingId;
    
    @TableField("building_name")
    private String buildingName;
    
    @TableField("room_id")
    private Long roomId;
    
    @TableField("room_number")
    private String roomNumber;
    
    @TableField("bed_number")
    private String bedNumber;
    
    private Integer priority;
    
    private String status;
    
    private String remark;
    
    @TableField("select_time")
    private LocalDateTime selectTime;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
