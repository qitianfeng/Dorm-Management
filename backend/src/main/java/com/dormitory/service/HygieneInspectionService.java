package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.HygieneInspectionRequest;
import com.dormitory.dto.response.HygieneInspectionResponse;

import java.util.List;

/**
 * 卫生检查服务接口
 */
public interface HygieneInspectionService {
    
    /**
     * 创建检查记录
     */
    HygieneInspectionResponse create(HygieneInspectionRequest request);
    
    /**
     * 更新检查记录
     */
    HygieneInspectionResponse update(Long id, HygieneInspectionRequest request);
    
    /**
     * 删除检查记录
     */
    void delete(Long id);
    
    /**
     * 获取详情
     */
    HygieneInspectionResponse getById(Long id);
    
    /**
     * 根据房间ID获取检查记录
     */
    List<HygieneInspectionResponse> getByRoomId(Long roomId);
    
    /**
     * 根据楼栋ID获取检查记录
     */
    List<HygieneInspectionResponse> getByBuildingId(Long buildingId);
    
    /**
     * 分页查询
     */
    PageResult<HygieneInspectionResponse> getPage(Integer page, Integer size, Long buildingId, Long roomId, String grade);
}
