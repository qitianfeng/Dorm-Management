package com.dormitory.controller;

import com.dormitory.annotation.RequirePermission;
import com.dormitory.common.Result;
import com.dormitory.entity.Role;
import com.dormitory.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    @Operation(summary = "获取所有角色")
    @GetMapping
    @RequirePermission("role")
    public Result<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
    
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    @RequirePermission("role")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }
    
    @Operation(summary = "创建角色")
    @PostMapping
    @RequirePermission("role")
    public Result<Role> create(@RequestBody Role role) {
        Role created = roleService.create(role);
        return Result.success("创建成功", created);
    }
    
    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @RequirePermission("role")
    public Result<Role> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        Role updated = roleService.update(role);
        return Result.success("更新成功", updated);
    }
    
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @RequirePermission("role")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success("删除成功", null);
    }
    
    @Operation(summary = "获取用户角色")
    @GetMapping("/user/{userId}")
    @RequirePermission("role")
    public Result<List<Role>> getUserRoles(@PathVariable Long userId) {
        List<Role> roles = roleService.getUserRoles(userId);
        return Result.success(roles);
    }
}
