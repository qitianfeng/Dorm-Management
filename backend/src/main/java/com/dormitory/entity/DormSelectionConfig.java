package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选宿舍配置实体
 */
@Data
@TableName("dorm_selection_config")
public class DormSelectionConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("config_name")
    private String configName;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("academic_year")
    private String academicYear;
    
    @TableField("semester")
    private String semester;
    
    @TableField("building_ids")
    private String buildingIds;
    
    @TableField("gender_restriction")
    private String genderRestriction;
    
    @TableField("allowed_colleges")
    private String allowedColleges;
    
    @TableField("allowed_grades")
    private String allowedGrades;
    
    @TableField("max_select_count")
    private Integer maxSelectCount;
    
    private String status;
    
    private String description;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    // 版本号（乐观锁）
    @TableField("version")
    private Integer version;
}
