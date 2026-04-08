package com.dormitory.controller;

import com.dormitory.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 临时数据库初始化控制器（仅用于开发环境）
 */
@Slf4j
@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DatabaseInitController {
    
    private final JdbcTemplate jdbcTemplate;
    
    @PostMapping("/init-dorm-selection")
    public Result<String> initDormSelectionTables() {
        try {
            // 1. 创建选宿舍配置表
            String createConfigTable = """
                CREATE TABLE IF NOT EXISTS dorm_selection_config (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
                    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
                    start_time DATETIME NOT NULL COMMENT '开始时间',
                    end_time DATETIME NOT NULL COMMENT '结束时间',
                    academic_year VARCHAR(20) COMMENT '学年',
                    semester VARCHAR(20) COMMENT '学期',
                    gender_restriction VARCHAR(20) DEFAULT 'ALL' COMMENT '性别限制',
                    allowed_colleges TEXT COMMENT '允许的学院',
                    allowed_grades VARCHAR(100) COMMENT '允许的年级',
                    building_ids VARCHAR(500) COMMENT '可选楼栋ID',
                    max_select_count INT DEFAULT 1 COMMENT '最大可选志愿数',
                    status VARCHAR(20) DEFAULT 'INACTIVE' COMMENT '状态',
                    description TEXT COMMENT '描述说明',
                    version INT DEFAULT 0 COMMENT '乐观锁版本号',
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='选宿舍配置表'
                """;
            jdbcTemplate.execute(createConfigTable);
            log.info("Created dorm_selection_config table");
            
            // 2. 创建选宿舍记录表
            String createRecordTable = """
                CREATE TABLE IF NOT EXISTS dorm_selection_record (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
                    config_id BIGINT NOT NULL COMMENT '配置ID',
                    student_id BIGINT NOT NULL COMMENT '学生ID',
                    student_name VARCHAR(50) COMMENT '学生姓名',
                    building_id BIGINT COMMENT '楼栋ID',
                    building_name VARCHAR(50) COMMENT '楼栋名称',
                    room_id BIGINT COMMENT '房间ID',
                    room_number VARCHAR(50) COMMENT '房间号',
                    bed_number VARCHAR(10) COMMENT '床位号',
                    priority INT DEFAULT 0 COMMENT '志愿优先级',
                    status VARCHAR(20) DEFAULT 'SELECTED' COMMENT '状态',
                    remark TEXT COMMENT '备注',
                    select_time DATETIME COMMENT '选择时间',
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    KEY idx_config_id (config_id),
                    KEY idx_student_id (student_id),
                    KEY idx_room_id (room_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='选宿舍记录表'
                """;
            jdbcTemplate.execute(createRecordTable);
            log.info("Created dorm_selection_record table");
            
            // 3. 添加gender字段到sys_user表（使用更可靠的方式）
            try {
                // 先检查字段是否存在
                Integer genderExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_user' AND COLUMN_NAME = 'gender'",
                    Integer.class
                );
                if (genderExists == 0) {
                    jdbcTemplate.execute("ALTER TABLE sys_user ADD COLUMN gender VARCHAR(10) COMMENT '性别' AFTER email");
                    log.info("Added gender column to sys_user table");
                } else {
                    log.info("Gender column already exists in sys_user table");
                }
            } catch (Exception e) {
                log.error("Failed to add gender column: {}", e.getMessage());
            }
            
            // 4. 更新学生性别数据
            try {
                int updated1 = jdbcTemplate.update("UPDATE sys_user SET gender = 'MALE' WHERE username IN ('stu001', 'stu002') AND (gender IS NULL OR gender = '')");
                int updated2 = jdbcTemplate.update("UPDATE sys_user SET gender = 'FEMALE' WHERE username IN ('stu003', 'stu004') AND (gender IS NULL OR gender = '')");
                log.info("Updated student gender data: {} males, {} females", updated1, updated2);
            } catch (Exception e) {
                log.error("Failed to update gender data: {}", e.getMessage());
            }
            
            // 5. 添加gender_type字段到dorm_building表
            try {
                jdbcTemplate.execute("ALTER TABLE dorm_building ADD COLUMN gender_type VARCHAR(20) DEFAULT 'MIXED' COMMENT '性别类型' AFTER building_type");
                log.info("Added gender_type column to dorm_building table");
            } catch (Exception e) {
                log.info("Gender_type column already exists or error: {}", e.getMessage());
            }
            
            // 6. 插入示例选宿舍配置
            int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dorm_selection_config WHERE config_name = '2024年秋季新生选宿舍'", 
                Integer.class
            );
            if (count == 0) {
                jdbcTemplate.update("""
                    INSERT INTO dorm_selection_config 
                    (config_name, start_time, end_time, academic_year, semester, gender_restriction, 
                     allowed_colleges, allowed_grades, building_ids, max_select_count, status, description) 
                    VALUES 
                    ('2024年秋季新生选宿舍', 
                     DATE_SUB(NOW(), INTERVAL 30 DAY), 
                     DATE_ADD(NOW(), INTERVAL 30 DAY), 
                     '2024-2025', '第一学期', 'ALL', 
                     '计算机学院,外国语学院,经济学院', '2024', '1,2,3,4,5', 1, 'ACTIVE', 
                     '2024年秋季入学新生宿舍选择')
                    """);
                log.info("Inserted sample dorm selection config");
            }
            
            return Result.success("数据库初始化成功！选宿舍表已创建。");
        } catch (Exception e) {
            log.error("Failed to initialize database", e);
            return Result.error("初始化失败: " + e.getMessage());
        }
    }
}
