package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.AnnouncementRequest;
import com.dormitory.dto.response.AnnouncementResponse;

import java.util.List;

/**
 * 公告通知服务接口
 */
public interface AnnouncementService {
    
    AnnouncementResponse create(AnnouncementRequest request, Long publisherId);
    
    AnnouncementResponse update(Long id, AnnouncementRequest request);
    
    void delete(Long id);
    
    AnnouncementResponse getById(Long id);
    
    List<AnnouncementResponse> getPublished();
    
    PageResult<AnnouncementResponse> getPage(Integer page, Integer size, String type, String keyword);
    
    AnnouncementResponse publish(Long id);
    
    AnnouncementResponse incrementViewCount(Long id);
}
