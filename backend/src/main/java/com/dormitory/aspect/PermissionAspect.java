package com.dormitory.aspect;

import com.dormitory.annotation.RequirePermission;
import com.dormitory.entity.Permission;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.PermissionMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 权限检查切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    
    private final PermissionMapper permissionMapper;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        log.info("========== PermissionAspect拦截到请求 ==========");
        
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("用户未登录");
            throw new BusinessException("用户未登录");
        }
        
        String username = authentication.getName();
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取用户权限
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());
        List<String> permissionCodes = permissions.stream()
                .map(Permission::getCode)
                .toList();
        
        // 检查权限
        String[] requiredPermissions = requirePermission.value();
        boolean requireAll = requirePermission.requireAll();
        
        boolean hasPermission;
        if (requireAll) {
            // 需要所有权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(permissionCodes::contains);
        } else {
            // 只需任一权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(permissionCodes::contains);
        }
        
        if (!hasPermission) {
            String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
            log.warn("用户 {} 尝试访问未授权的方法 {}，需要权限: {}", 
                    username, methodName, Arrays.toString(requiredPermissions));
            throw new BusinessException("权限不足");
        }
        
        return joinPoint.proceed();
    }
}
