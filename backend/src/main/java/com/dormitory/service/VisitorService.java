package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.VisitorRequest;
import com.dormitory.dto.response.VisitorResponse;

import java.util.List;

/**
 * 访客登记服务接口
 */
public interface VisitorService {
    
    /**
     * 登记访客
     */
    VisitorResponse create(VisitorRequest request);
    
    /**
     * 更新访客信息
     */
    VisitorResponse update(Long id, VisitorRequest request);
    
    /**
     * 访客离开
     */
    VisitorResponse leave(Long id);
    
    /**
     * 删除记录
     */
    void delete(Long id);
    
    /**
     * 获取详情
     */
    VisitorResponse getById(Long id);
    
    /**
     * 根据房间获取访客
     */
    List<VisitorResponse> getByRoomId(Long roomId);
    
    /**
     * 获取访问中的访客
     */
    List<VisitorResponse> getVisiting();
    
    /**
     * 分页查询
     */
    PageResult<VisitorResponse> getPage(Integer page, Integer size, Long roomId, String status, String keyword);
}
