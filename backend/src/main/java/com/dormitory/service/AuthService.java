package com.dormitory.service;

import com.dormitory.dto.request.LoginRequest;
import com.dormitory.dto.request.RegisterRequest;
import com.dormitory.dto.response.JwtResponse;
import com.dormitory.dto.response.UserResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    JwtResponse login(LoginRequest request);
    
    /**
     * 用户注册
     */
    UserResponse register(RegisterRequest request);
    
    /**
     * 获取当前用户信息
     */
    UserResponse getCurrentUser();
    
    /**
     * 刷新Token
     */
    JwtResponse refreshToken(String oldToken);
}
