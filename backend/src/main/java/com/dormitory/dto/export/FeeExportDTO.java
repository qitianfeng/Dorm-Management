package com.dormitory.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缴费记录导出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeExportDTO {
    
    @ExcelProperty("学号")
    @ColumnWidth(15)
    private String studentIdNumber;
    
    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String studentName;
    
    @ExcelProperty("费用类型")
    @ColumnWidth(15)
    private String feeType;
    
    @ExcelProperty("金额")
    @ColumnWidth(12)
    private String amount;
    
    @ExcelProperty("应缴时间")
    @ColumnWidth(18)
    private String dueTime;
    
    @ExcelProperty("缴费时间")
    @ColumnWidth(18)
    private String paidTime;
    
    @ExcelProperty("状态")
    @ColumnWidth(10)
    private String status;
    
    @ExcelProperty("支付方式")
    @ColumnWidth(12)
    private String paymentMethod;
    
    @ExcelProperty("交易流水号")
    @ColumnWidth(25)
    private String transactionId;
    
    @ExcelProperty("操作人")
    @ColumnWidth(12)
    private String operatorName;
}
