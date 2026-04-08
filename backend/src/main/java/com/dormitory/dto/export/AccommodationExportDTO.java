package com.dormitory.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生住宿导出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationExportDTO {
    
    @ExcelProperty("学号")
    @ColumnWidth(15)
    private String studentIdNumber;
    
    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String studentName;
    
    @ExcelProperty("院系")
    @ColumnWidth(20)
    private String department;
    
    @ExcelProperty("班级")
    @ColumnWidth(20)
    private String className;
    
    @ExcelProperty("楼栋")
    @ColumnWidth(15)
    private String buildingName;
    
    @ExcelProperty("房间号")
    @ColumnWidth(10)
    private String roomNumber;
    
    @ExcelProperty("床位号")
    @ColumnWidth(10)
    private String bedNumber;
    
    @ExcelProperty("入住日期")
    @ColumnWidth(15)
    private String checkInDate;
    
    @ExcelProperty("押金")
    @ColumnWidth(10)
    private String deposit;
    
    @ExcelProperty("状态")
    @ColumnWidth(10)
    private String status;
}
