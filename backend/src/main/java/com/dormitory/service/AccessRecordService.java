package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.entity.AccessRecord;

/**
 * 门禁记录服务接口
 */
public interface AccessRecordService {
    
    /**
     * 分页查询门禁记录
     */
    PageResult<AccessRecord> getPage(Integer page, Integer size, String accessType, String keyword);
    
    /**
     * 获取详情
     */
    AccessRecord getById(Long id);
    
    /**
     * 删除记录
     */
    void delete(Long id);
}
