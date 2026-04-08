package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    
    /**
     * 根据用户ID查询权限列表
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 'ACTIVE' " +
            "ORDER BY p.sort")
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.status = 'ACTIVE' " +
            "ORDER BY p.sort")
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 查询所有菜单权限
     */
    @Select("SELECT * FROM sys_permission WHERE type = 'MENU' AND status = 'ACTIVE' ORDER BY sort")
    List<Permission> selectAllMenus();
    
    /**
     * 根据父ID查询子权限
     */
    @Select("SELECT * FROM sys_permission WHERE parent_id = #{parentId} AND status = 'ACTIVE' ORDER BY sort")
    List<Permission> selectByParentId(@Param("parentId") Long parentId);
}
