package com.dormitory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.Result;
import com.dormitory.entity.User;
import com.dormitory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<Page<User>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String status) {
        
        Page<User> pageParam = new Page<>(page, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like("real_name", realName);
        }
        if (userType != null && !userType.isEmpty()) {
            wrapper.eq("user_type", userType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("create_time");
        Page<User> result = userService.page(pageParam, wrapper);
        
        // 清除密码字段
        result.getRecords().forEach(u -> u.setPassword(null));
        
        return Result.success(result);
    }
    
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
    
    @Operation(summary = "创建用户")
    @PostMapping
    public Result<User> create(@RequestBody User user) {
        // 检查用户名是否已存在
        User existing = userService.getByUsername(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        user.setPassword(null);
        return Result.success("创建成功", user);
    }
    
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (existing == null) {
            return Result.error("用户不存在");
        }
        
        // 只更新允许修改的字段
        existing.setRealName(user.getRealName());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        existing.setUserType(user.getUserType());
        existing.setStatus(user.getStatus());
        
        userService.updateById(existing);
        existing.setPassword(null);
        return Result.success("更新成功", existing);
    }
    
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功", null);
    }
    
    @Operation(summary = "重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 重置密码为123456
        user.setPassword(passwordEncoder.encode("123456"));
        userService.updateById(user);
        return Result.success("密码已重置为: 123456", null);
    }
    
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        // 获取当前登录用户
        // 这里简化处理，实际应该从SecurityContext获取
        User user = userService.getById(request.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateById(user);
        return Result.success("密码修改成功", null);
    }
    
    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user) {
        User existing = userService.getById(user.getId());
        if (existing == null) {
            return Result.error("用户不存在");
        }
        
        existing.setRealName(user.getRealName());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        
        userService.updateById(existing);
        existing.setPassword(null);
        return Result.success("更新成功", existing);
    }
    
    /**
     * 修改密码请求DTO
     */
    @lombok.Data
    public static class ChangePasswordRequest {
        private Long userId;
        private String oldPassword;
        private String newPassword;
    }
}
