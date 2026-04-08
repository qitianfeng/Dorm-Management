package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.entity.Role;
import com.dormitory.entity.UserRole;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.RoleMapper;
import com.dormitory.mapper.UserRoleMapper;
import com.dormitory.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    
    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }
    
    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }
    
    @Override
    public Role create(Role role) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, role.getRoleCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }
        
        roleMapper.insert(role);
        return role;
    }
    
    @Override
    public Role update(Role role) {
        Role existing = roleMapper.selectById(role.getId());
        if (existing == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 检查角色编码是否与其他角色重复
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, role.getRoleCode())
                .ne(Role::getId, role.getId());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }
        
        roleMapper.updateById(role);
        return role;
    }
    
    @Override
    public void delete(Long id) {
        // 检查是否为系统内置角色
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if ("ADMIN".equals(role.getRoleCode())) {
            throw new BusinessException("系统管理员角色不能删除");
        }
        
        // 删除用户角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleId, id);
        userRoleMapper.delete(wrapper);
        
        // 删除角色
        roleMapper.deleteById(id);
    }
    
    @Override
    public List<Role> getUserRoles(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        if (userRoles.isEmpty()) {
            return List.of();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        return roleMapper.selectBatchIds(roleIds);
    }
}
