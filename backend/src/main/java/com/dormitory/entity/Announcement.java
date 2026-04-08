package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_announcement")
public class Announcement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    private String type;
    
    private String priority;
    
    private String status;
    
    @TableField("publish_time")
    private LocalDateTime publishTime;
    
    @TableField("expire_time")
    private LocalDateTime expireTime;
    
    @TableField("target_audience")
    private String targetAudience;
    
    private String attachments;
    
    @TableField("publisher_id")
    private Long publisherId;
    
    @TableField("publisher_name")
    private String publisherName;
    
    @TableField("view_count")
    private Integer viewCount;
    
    @TableField("is_top")
    private Boolean isTop;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
