package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.DormSelectionRequest;
import com.dormitory.entity.DormSelectionConfig;
import com.dormitory.entity.DormSelectionRecord;

import java.util.List;

/**
 * 选宿舍服务接口
 */
public interface DormSelectionService {
    
    /**
     * 获取当前可用的选宿舍配置
     */
    DormSelectionConfig getActiveConfig();
    
    /**
     * 获取可选的房间列表
     */
    List<Object> getAvailableRooms(Long configId, Long buildingId);
    
    /**
     * 学生选择宿舍
     */
    DormSelectionRecord selectRoom(Long studentId, DormSelectionRequest request);
    
    /**
     * 获取学生的选宿舍记录
     */
    DormSelectionRecord getStudentSelection(Long studentId);
    
    /**
     * 取消选宿舍
     */
    void cancelSelection(Long studentId);
    
    /**
     * 自动分配宿舍（倒计时结束后）
     */
    DormSelectionRecord autoAllocate(Long studentId);
    
    /**
     * 获取选宿舍记录列表（管理端）
     */
    PageResult<DormSelectionRecord> getSelectionRecords(Integer page, Integer size, Long configId, Long buildingId, String keyword);
}
