package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.entity.Permission;
import com.dormitory.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 */
@Tag(name = "权限管理", description = "权限管理相关接口")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    
    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/menus")
    public Result<List<Permission>> getCurrentUserMenus() {
        // TODO: 从SecurityContext获取当前用户ID
        // 暂时返回所有菜单
        List<Permission> menus = permissionService.getAllPermissionTree();
        return Result.success(menus);
    }
    
    @Operation(summary = "获取所有权限树")
    @GetMapping("/tree")
    public Result<List<Permission>> getAllPermissionTree() {
        List<Permission> tree = permissionService.getAllPermissionTree();
        return Result.success(tree);
    }
    
    @Operation(summary = "获取角色权限")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getRolePermissions(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getRolePermissions(roleId);
        return Result.success(permissions);
    }
    
    @Operation(summary = "分配角色权限")
    @PostMapping("/role/{roleId}")
    public Result<Void> assignRolePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        permissionService.assignRolePermissions(roleId, permissionIds);
        return Result.success("权限分配成功", null);
    }
    
    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }
    
    @Operation(summary = "创建权限")
    @PostMapping
    public Result<Permission> create(@RequestBody Permission permission) {
        Permission created = permissionService.create(permission);
        return Result.success("创建成功", created);
    }
    
    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    public Result<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        Permission updated = permissionService.update(permission);
        return Result.success("更新成功", updated);
    }
    
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success("删除成功", null);
    }
}
