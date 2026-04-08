package com.dormitory.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报修记录导出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairExportDTO {
    
    @ExcelProperty("申请人")
    @ColumnWidth(12)
    private String studentName;
    
    @ExcelProperty("楼栋")
    @ColumnWidth(15)
    private String buildingName;
    
    @ExcelProperty("房间号")
    @ColumnWidth(10)
    private String roomNumber;
    
    @ExcelProperty("报修类型")
    @ColumnWidth(15)
    private String repairType;
    
    @ExcelProperty("问题描述")
    @ColumnWidth(40)
    private String description;
    
    @ExcelProperty("优先级")
    @ColumnWidth(10)
    private String priority;
    
    @ExcelProperty("状态")
    @ColumnWidth(10)
    private String status;
    
    @ExcelProperty("处理人")
    @ColumnWidth(12)
    private String handlerName;
    
    @ExcelProperty("处理结果")
    @ColumnWidth(30)
    private String handleResult;
    
    @ExcelProperty("维修费用")
    @ColumnWidth(12)
    private String repairCost;
    
    @ExcelProperty("申请时间")
    @ColumnWidth(18)
    private String createTime;
}
