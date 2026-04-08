package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    @TableField("real_name")
    private String realName;
    
    private String phone;
    
    private String email;
    
    private String gender;
    
    @TableField("student_id")
    private String studentId;
    
    private String department;
    
    @TableField("class_name")
    private String className;
    
    @TableField("dept_id")
    private Long deptId;
    
    @TableField("user_type")
    private String userType;
    
    private String status;
    
    private String avatar;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
