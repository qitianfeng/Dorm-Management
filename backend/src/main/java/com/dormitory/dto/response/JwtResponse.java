package com.dormitory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * JWT认证响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String realName;
    private String userType;
    private List<String> roles;
    private List<String> permissions;
}
