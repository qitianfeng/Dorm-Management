package com.dormitory.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于控制数据访问范围
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    
    /**
     * 数据权限类型
     */
    DataScopeType value() default DataScopeType.ALL;
    
    /**
     * 部门表的别名
     */
    String deptAlias() default "d";
    
    /**
     * 用户表的别名
     */
    String userAlias() default "u";
    
    /**
     * 权限字符
     */
    String permission() default "";
    
    /**
     * 数据权限类型枚举
     */
    enum DataScopeType {
        /**
         * 全部数据权限
         */
        ALL,
        
        /**
         * 自定义数据权限
         */
        CUSTOM,
        
        /**
         * 本部门数据权限
         */
        DEPT,
        
        /**
         * 本部门及以下数据权限
         */
        DEPT_AND_CHILD,
        
        /**
         * 仅本人数据权限
         */
        SELF
    }
}
