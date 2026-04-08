package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.DisciplinaryRequest;
import com.dormitory.dto.response.DisciplinaryResponse;

/**
 * 违纪记录服务接口
 */
public interface DisciplinaryService {
    
    /**
     * 创建违纪记录
     */
    DisciplinaryResponse create(DisciplinaryRequest request);
    
    /**
     * 更新违纪记录
     */
    DisciplinaryResponse update(Long id, DisciplinaryRequest request);
    
    /**
     * 删除违纪记录
     */
    void delete(Long id);
    
    /**
     * 获取详情
     */
    DisciplinaryResponse getById(Long id);
    
    /**
     * 分页查询
     */
    PageResult<DisciplinaryResponse> getPage(Integer page, Integer size, String keyword, String type, String status);
}
