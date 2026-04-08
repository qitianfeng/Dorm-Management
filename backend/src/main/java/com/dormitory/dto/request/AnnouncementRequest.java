package com.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告通知请求DTO
 */
@Data
public class AnnouncementRequest {
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String type;
    
    private String priority;
    
    private String status;
    
    private LocalDateTime publishTime;
    
    private LocalDateTime expireTime;
    
    private String targetAudience;
    
    private String attachments;
    
    private Boolean isTop;
}
