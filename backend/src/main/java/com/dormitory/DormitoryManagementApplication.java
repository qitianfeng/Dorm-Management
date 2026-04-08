package com.dormitory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 宿舍管理系统主启动类
 * 
 * @author Dormitory Management System
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
public class DormitoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryManagementApplication.class, args);
        System.out.println("====================================");
        System.out.println("宿舍管理系统启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("Druid监控地址: http://localhost:8080/api/druid/index.html");
        System.out.println("====================================");
    }
}
