package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告通知响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    
    private Long id;
    private String title;
    private String content;
    private String type;
    private String priority;
    private String status;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private String targetAudience;
    private String attachments;
    private Long publisherId;
    private String publisherName;
    private Integer viewCount;
    private Boolean isTop;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
