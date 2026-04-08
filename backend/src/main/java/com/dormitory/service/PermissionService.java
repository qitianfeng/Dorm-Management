package com.dormitory.service;

import com.dormitory.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {
    
    /**
     * 获取用户权限列表
     */
    List<Permission> getUserPermissions(Long userId);
    
    /**
     * 获取用户菜单树
     */
    List<Permission> getUserMenuTree(Long userId);
    
    /**
     * 获取角色权限列表
     */
    List<Permission> getRolePermissions(Long roleId);
    
    /**
     * 获取所有菜单
     */
    List<Permission> getAllMenus();
    
    /**
     * 获取所有权限树
     */
    List<Permission> getAllPermissionTree();
    
    /**
     * 分配角色权限
     */
    void assignRolePermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 获取权限详情
     */
    Permission getById(Long id);
    
    /**
     * 创建权限
     */
    Permission create(Permission permission);
    
    /**
     * 更新权限
     */
    Permission update(Permission permission);
    
    /**
     * 删除权限
     */
    void delete(Long id);
}
