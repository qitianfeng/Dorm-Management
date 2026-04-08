package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.dto.request.LoginRequest;
import com.dormitory.dto.request.RegisterRequest;
import com.dormitory.dto.response.JwtResponse;
import com.dormitory.dto.response.UserResponse;
import com.dormitory.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、注册、Token刷新等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("=== Login request received: " + request.getUsername());
        JwtResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }
    
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return Result.success("注册成功", response);
    }
    
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return Result.success(response);
    }
    
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<JwtResponse> refreshToken(@RequestHeader("Authorization") String token) {
        JwtResponse response = authService.refreshToken(token);
        return Result.success("Token刷新成功", response);
    }
}
