package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.HandleRepairRequest;
import com.dormitory.dto.request.RepairRequestDto;
import com.dormitory.dto.response.RepairResponse;

import java.util.List;

/**
 * 报修服务接口
 */
public interface RepairService {
    
    /**
     * 提交报修申请
     */
    RepairResponse create(RepairRequestDto request);
    
    /**
     * 处理报修
     */
    RepairResponse handle(Long id, HandleRepairRequest request);
    
    /**
     * 取消报修
     */
    RepairResponse cancel(Long id);
    
    /**
     * 获取详情
     */
    RepairResponse getById(Long id);
    
    /**
     * 根据学生ID获取报修列表
     */
    List<RepairResponse> getByStudentId(Long studentId);
    
    /**
     * 根据状态获取报修列表
     */
    List<RepairResponse> getByStatus(String status);
    
    /**
     * 分页查询
     */
    PageResult<RepairResponse> getPage(Integer page, Integer size, Long buildingId, String status, String keyword);
}
