package com.dormitory.aspect;

import com.dormitory.annotation.DataScope;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.ManagerBuildingMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.security.JwtTokenProvider;
import com.dormitory.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 数据权限切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {
    
    private final PermissionService permissionService;
    private final UserMapper userMapper;
    private final ManagerBuildingMapper managerBuildingMapper;
    
    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint joinPoint, DataScope dataScope) throws Throwable {
        log.info("========== DataScopeAspect拦截到请求 ==========");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        String username = authentication.getName();
        User user = userMapper.selectByUsername(username);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        log.info("DataScope检查 - 用户: {}, 类型: {}", username, user.getUserType());
        
        // 管理员拥有所有数据权限
        if ("ADMIN".equals(user.getUserType())) {
            log.info("管理员拥有所有数据权限，跳过数据权限过滤");
            return joinPoint.proceed();
        }
        
        // 宿管只能管理自己负责的楼栋
        if ("DORM_MANAGER".equals(user.getUserType())) {
            List<Long> buildingIds = managerBuildingMapper.selectBuildingIdsByUserId(user.getId());
            log.info("宿管数据权限 - 用户ID: {}, 管理的楼栋: {}", user.getId(), buildingIds);
            DataScopeContext.setCurrentUserBuildingIds(buildingIds);
        }
        
        // 获取用户权限
        List<String> permissions = permissionService.getUserPermissions(user.getId())
                .stream()
                .map(p -> p.getCode())
                .toList();
        
        // 检查是否有自定义权限
        String requiredPermission = dataScope.permission();
        if (!requiredPermission.isEmpty() && !permissions.contains(requiredPermission)) {
            // 没有自定义权限，使用数据权限过滤
            DataScope.DataScopeType scopeType = dataScope.value();
            
            log.info("应用数据权限过滤 - 类型: {}, 用户ID: {}", scopeType, user.getId());
            
            // 根据数据权限类型添加过滤条件
            switch (scopeType) {
                case DEPT:
                    // 添加部门过滤条件
                    Long deptId = user.getDeptId();
                    if (deptId != null) {
                        log.info("应用部门数据权限: userId={}, deptId={}", user.getId(), deptId);
                        DataScopeContext.setCurrentDeptId(deptId);
                    }
                    break;
                case DEPT_AND_CHILD:
                    // 添加部门及子部门过滤条件
                    Long parentDeptId = user.getDeptId();
                    if (parentDeptId != null) {
                        log.info("应用部门及子部门数据权限: userId={}, deptId={}", user.getId(), parentDeptId);
                        DataScopeContext.setCurrentDeptId(parentDeptId);
                        DataScopeContext.setIncludeChildDepts(true);
                    }
                    break;
                case SELF:
                    // 添加本人数据过滤条件
                    // 如果是宿管，且已设置楼栋ID，则使用楼栋权限而非本人权限
                    List<Long> mgrBuildingIds = DataScopeContext.getCurrentUserBuildingIds();
                    if (mgrBuildingIds != null && !mgrBuildingIds.isEmpty()) {
                        log.info("宿管使用楼栋数据权限，跳过本人权限: buildingIds={}", mgrBuildingIds);
                    } else {
                        log.info("应用本人数据权限: userId={}", user.getId());
                        // 将用户ID存入ThreadLocal，供Mapper使用
                        DataScopeContext.setCurrentUserId(user.getId());
                    }
                    break;
                case CUSTOM:
                    // 自定义权限，需要检查
                    log.debug("应用自定义数据权限: userId={}", user.getId());
                    break;
                case ALL:
                default:
                    // 全部数据权限，不做过滤
                    log.debug("全部数据权限，不做过滤");
                    break;
            }
        } else {
            log.info("用户拥有自定义权限 {}，跳过数据权限过滤", requiredPermission);
        }
        
        try {
            return joinPoint.proceed();
        } finally {
            // 清理ThreadLocal
            DataScopeContext.clear();
        }
    }
    
    /**
     * 数据权限上下文，用于在当前线程中传递用户ID
     */
    public static class DataScopeContext {
        private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
        private static final ThreadLocal<List<Long>> CURRENT_USER_BUILDING_IDS = new ThreadLocal<>();
        private static final ThreadLocal<Long> CURRENT_DEPT_ID = new ThreadLocal<>();
        private static final ThreadLocal<Boolean> INCLUDE_CHILD_DEPTS = new ThreadLocal<>();
        
        public static void setCurrentUserId(Long userId) {
            CURRENT_USER_ID.set(userId);
        }
        
        public static Long getCurrentUserId() {
            return CURRENT_USER_ID.get();
        }
        
        public static void setCurrentUserBuildingIds(List<Long> buildingIds) {
            CURRENT_USER_BUILDING_IDS.set(buildingIds);
        }
        
        public static List<Long> getCurrentUserBuildingIds() {
            return CURRENT_USER_BUILDING_IDS.get();
        }
        
        public static void setCurrentDeptId(Long deptId) {
            CURRENT_DEPT_ID.set(deptId);
        }
        
        public static Long getCurrentDeptId() {
            return CURRENT_DEPT_ID.get();
        }
        
        public static void setIncludeChildDepts(boolean include) {
            INCLUDE_CHILD_DEPTS.set(include);
        }
        
        public static Boolean getIncludeChildDepts() {
            return INCLUDE_CHILD_DEPTS.get();
        }
        
        public static void clear() {
            CURRENT_USER_ID.remove();
            CURRENT_USER_BUILDING_IDS.remove();
            CURRENT_DEPT_ID.remove();
            INCLUDE_CHILD_DEPTS.remove();
        }
    }
}
