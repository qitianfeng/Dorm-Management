package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@Tag(name = "文件上传", description = "文件上传接口")
@RestController
@RequestMapping("/upload")
public class FileUploadController {
    
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    @Value("${file.upload.max-size:10485760}")
    private Long maxSize;
    
    @Operation(summary = "单文件上传")
    @PostMapping("/single")
    public Result<String> uploadSingle(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超过限制（最大10MB）");
        }
        
        String filePath = saveFile(file);
        return Result.success("上传成功", filePath);
    }
    
    @Operation(summary = "多文件上传")
    @PostMapping("/multiple")
    public Result<List<String>> uploadMultiple(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BusinessException("上传文件不能为空");
        }
        
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                if (file.getSize() > maxSize) {
                    throw new BusinessException("文件 " + file.getOriginalFilename() + " 大小超过限制");
                }
                filePaths.add(saveFile(file));
            }
        }
        
        return Result.success("上传成功", filePaths);
    }
    
    @Operation(summary = "图片上传")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传图片不能为空");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }
        
        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("图片大小超过限制（最大10MB）");
        }
        
        String filePath = saveFile(file, "images");
        return Result.success("上传成功", filePath);
    }
    
    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file) {
        return saveFile(file, "files");
    }
    
    /**
     * 保存文件到指定目录
     */
    private String saveFile(MultipartFile file, String subDir) {
        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "unknown";
            }
            
            // 获取文件扩展名
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            
            // 生成新文件名
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 按日期创建子目录
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            
            // 创建完整的文件保存路径
            String fullPath = uploadPath + File.separator + subDir + File.separator + datePath;
            File directory = new File(fullPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(fullPath + File.separator + newFilename);
            file.transferTo(destFile);
            
            // 返回相对路径（用于前端访问）
            String relativePath = "/api/upload/" + subDir + "/" + datePath + "/" + newFilename;
            log.info("文件上传成功: {}", relativePath);
            
            return relativePath;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
}
