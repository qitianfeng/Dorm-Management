package com.dormitory.controller;

import com.dormitory.annotation.RequirePermission;
import com.dormitory.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-aop")
public class TestAopController {
    
    @GetMapping("/role")
    @RequirePermission("system:role")
    public Result<String> testRole() {
        return Result.success("测试成功 - system:role");
    }
    
    @GetMapping("/public")
    public Result<String> testPublic() {
        return Result.success("测试成功 - public");
    }
}
