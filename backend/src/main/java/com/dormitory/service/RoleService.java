package com.dormitory.service;

import com.dormitory.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {
    
    /**
     * 获取所有角色
     */
    List<Role> getAllRoles();
    
    /**
     * 获取角色详情
     */
    Role getById(Long id);
    
    /**
     * 创建角色
     */
    Role create(Role role);
    
    /**
     * 更新角色
     */
    Role update(Role role);
    
    /**
     * 删除角色
     */
    void delete(Long id);
    
    /**
     * 获取用户的角色列表
     */
    List<Role> getUserRoles(Long userId);
}
