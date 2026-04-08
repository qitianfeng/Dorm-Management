package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.entity.Permission;
import com.dormitory.entity.RolePermission;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.PermissionMapper;
import com.dormitory.mapper.RolePermissionMapper;
import com.dormitory.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 * 注意：Redis缓存已配置但需要启动Redis服务才能使用
 * 如果Redis未启动，缓存功能将被禁用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    
    @Override
    public List<Permission> getUserPermissions(Long userId) {
        log.debug("从数据库获取用户权限: userId={}", userId);
        return permissionMapper.selectPermissionsByUserId(userId);
    }
    
    @Override
    public List<Permission> getUserMenuTree(Long userId) {
        List<Permission> permissions = getUserPermissions(userId);
        
        // 筛选出菜单类型的权限
        List<Permission> menus = permissions.stream()
                .filter(p -> "MENU".equals(p.getType()))
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildTree(menus, 0L);
    }
    
    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        log.debug("从数据库获取角色权限: roleId={}", roleId);
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }
    
    @Override
    public List<Permission> getAllMenus() {
        return permissionMapper.selectAllMenus();
    }
    
    @Override
    public List<Permission> getAllPermissionTree() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getStatus, "ACTIVE")
                .orderByAsc(Permission::getSort);
        List<Permission> allPermissions = permissionMapper.selectList(wrapper);
        return buildTree(allPermissions, 0L);
    }
    
    @Override
    @Transactional
    public void assignRolePermissions(Long roleId, List<Long> permissionIds) {
        log.info("分配角色权限: roleId={}", roleId);
        
        // 先删除原有权限
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }
    
    @Override
    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }
    
    @Override
    public Permission create(Permission permission) {
        // 检查权限标识是否已存在
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getCode, permission.getCode());
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("权限标识已存在");
        }
        
        permission.setStatus("ACTIVE");
        permissionMapper.insert(permission);
        return permission;
    }
    
    @Override
    public Permission update(Permission permission) {
        Permission existing = permissionMapper.selectById(permission.getId());
        if (existing == null) {
            throw new BusinessException("权限不存在");
        }
        
        // 检查权限标识是否与其他权限重复
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getCode, permission.getCode())
                .ne(Permission::getId, permission.getId());
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("权限标识已存在");
        }
        
        permissionMapper.updateById(permission);
        return permission;
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        // 检查是否有子权限
        List<Permission> children = permissionMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子权限，无法删除");
        }
        
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getPermissionId, id);
        rolePermissionMapper.delete(wrapper);
        
        // 删除权限
        permissionMapper.deleteById(id);
    }
    
    /**
     * 构建权限树
     */
    private List<Permission> buildTree(List<Permission> permissions, Long parentId) {
        List<Permission> result = new ArrayList<>();
        
        Map<Long, List<Permission>> groupedByParent = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
        
        List<Permission> rootPermissions = groupedByParent.getOrDefault(parentId, new ArrayList<>());
        
        for (Permission root : rootPermissions) {
            root.setChildren(buildChildren(groupedByParent, root.getId()));
            result.add(root);
        }
        
        return result;
    }
    
    private List<Permission> buildChildren(Map<Long, List<Permission>> groupedByParent, Long parentId) {
        List<Permission> children = groupedByParent.getOrDefault(parentId, new ArrayList<>());
        for (Permission child : children) {
            child.setChildren(buildChildren(groupedByParent, child.getId()));
        }
        return children;
    }
}
