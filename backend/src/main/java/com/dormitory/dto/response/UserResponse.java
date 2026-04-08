package com.dormitory.dto.response;

import com.dormitory.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String studentId;
    private String department;
    private String className;
    private String userType;
    private String status;
    private String avatar;
    private LocalDateTime createTime;
}
