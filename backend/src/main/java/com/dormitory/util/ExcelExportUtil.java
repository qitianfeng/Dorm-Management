package com.dormitory.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Excel导出工具类
 */
@Slf4j
public class ExcelExportUtil {
    
    /**
     * 导出Excel
     *
     * @param response  HTTP响应
     * @param fileName  文件名
     * @param sheetName Sheet名称
     * @param clazz     数据类型
     * @param data      数据列表
     */
    public static <T> void export(HttpServletResponse response, String fileName, 
                                   String sheetName, Class<T> clazz, List<T> data) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            
            // 文件名URL编码
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
            
            // 写入Excel
            EasyExcel.write(response.getOutputStream(), clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);
            
            log.info("Excel导出成功: {}", fileName);
        } catch (IOException e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败: " + e.getMessage());
        }
    }
}
