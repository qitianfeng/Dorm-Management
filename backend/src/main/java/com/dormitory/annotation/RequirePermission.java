package com.dormitory.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于标记需要特定权限才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 需要的权限标识
     */
    String[] value() default {};
    
    /**
     * 是否需要满足所有权限（AND关系）
     * false: 满足任一权限即可（OR关系）
     */
    boolean requireAll() default false;
}
