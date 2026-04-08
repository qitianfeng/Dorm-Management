package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.entity.AccessRecord;
import com.dormitory.mapper.AccessRecordMapper;
import com.dormitory.service.AccessRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 门禁记录服务实现
 */
@Service
@RequiredArgsConstructor
public class AccessRecordServiceImpl implements AccessRecordService {
    
    private final AccessRecordMapper accessRecordMapper;
    
    @Override
    public PageResult<AccessRecord> getPage(Integer page, Integer size, String accessType, String keyword) {
        LambdaQueryWrapper<AccessRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(accessType)) {
            wrapper.eq(AccessRecord::getAccessType, accessType);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(AccessRecord::getUserName, keyword)
                .or()
                .like(AccessRecord::getBuildingName, keyword)
                .or()
                .like(AccessRecord::getDeviceLocation, keyword)
            );
        }
        wrapper.orderByDesc(AccessRecord::getAccessTime);
        
        IPage<AccessRecord> pageResult = accessRecordMapper.selectPage(
            new Page<>(page, size), wrapper);
        
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), 
            pageResult.getSize(), pageResult.getCurrent());
    }
    
    @Override
    public AccessRecord getById(Long id) {
        return accessRecordMapper.selectById(id);
    }
    
    @Override
    public void delete(Long id) {
        accessRecordMapper.deleteById(id);
    }
}
