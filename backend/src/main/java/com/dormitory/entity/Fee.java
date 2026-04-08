package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dorm_fee")
public class Fee {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("fee_type")
    private String feeType;
    
    private BigDecimal amount;
    
    @TableField("due_time")
    private LocalDateTime dueTime;
    
    @TableField("paid_time")
    private LocalDateTime paidTime;
    
    private String status;
    
    @TableField("payment_method")
    private String paymentMethod;
    
    @TableField("transaction_id")
    private String transactionId;
    
    private String remark;
    
    @TableField("operator_id")
    private Long operatorId;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
