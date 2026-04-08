package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.AnnouncementRequest;
import com.dormitory.dto.response.AnnouncementResponse;
import com.dormitory.entity.Announcement;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.AnnouncementMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    
    private final AnnouncementMapper announcementMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public AnnouncementResponse create(AnnouncementRequest request, Long publisherId) {
        User publisher = userMapper.selectById(publisherId);
        if (publisher == null) {
            throw new BusinessException("发布人不存在");
        }
        
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setType(request.getType() != null ? request.getType() : "NOTICE");
        announcement.setPriority(request.getPriority() != null ? request.getPriority() : "NORMAL");
        announcement.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        announcement.setPublishTime(request.getPublishTime());
        announcement.setExpireTime(request.getExpireTime());
        announcement.setTargetAudience(request.getTargetAudience());
        announcement.setAttachments(request.getAttachments());
        announcement.setPublisherId(publisherId);
        announcement.setPublisherName(publisher.getRealName());
        announcement.setIsTop(request.getIsTop() != null ? request.getIsTop() : false);
        announcement.setViewCount(0);
        
        announcementMapper.insert(announcement);
        return convertToResponse(announcement);
    }
    
    @Override
    @Transactional
    public AnnouncementResponse update(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        if (request.getType() != null) {
            announcement.setType(request.getType());
        }
        if (request.getPriority() != null) {
            announcement.setPriority(request.getPriority());
        }
        announcement.setPublishTime(request.getPublishTime());
        announcement.setExpireTime(request.getExpireTime());
        announcement.setTargetAudience(request.getTargetAudience());
        announcement.setAttachments(request.getAttachments());
        announcement.setIsTop(request.getIsTop());
        
        announcementMapper.updateById(announcement);
        return convertToResponse(announcement);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcementMapper.deleteById(id);
    }
    
    @Override
    public AnnouncementResponse getById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        return convertToResponse(announcement);
    }
    
    @Override
    public List<AnnouncementResponse> getPublished() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, "PUBLISHED");
        return announcementMapper.selectList(wrapper).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<AnnouncementResponse> getPage(Integer page, Integer size, String type, String keyword) {
        Page<Announcement> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            wrapper.eq(Announcement::getType, type);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Announcement::getTitle, keyword);
        }
        wrapper.orderByDesc(Announcement::getCreateTime);
        
        IPage<Announcement> pageResult = announcementMapper.selectPage(mpPage, wrapper);
        
        List<AnnouncementResponse> responses = pageResult.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    @Override
    @Transactional
    public AnnouncementResponse publish(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcement.setStatus("PUBLISHED");
        announcement.setPublishTime(LocalDateTime.now());
        
        announcementMapper.updateById(announcement);
        return convertToResponse(announcement);
    }
    
    @Override
    @Transactional
    public AnnouncementResponse incrementViewCount(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcement.setViewCount(announcement.getViewCount() + 1);
        announcementMapper.updateById(announcement);
        
        return convertToResponse(announcement);
    }
    
    private AnnouncementResponse convertToResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .type(announcement.getType())
                .priority(announcement.getPriority())
                .status(announcement.getStatus())
                .publishTime(announcement.getPublishTime())
                .expireTime(announcement.getExpireTime())
                .targetAudience(announcement.getTargetAudience())
                .attachments(announcement.getAttachments())
                .publisherId(announcement.getPublisherId())
                .publisherName(announcement.getPublisherName())
                .viewCount(announcement.getViewCount())
                .isTop(announcement.getIsTop())
                .createTime(announcement.getCreateTime())
                .updateTime(announcement.getUpdateTime())
                .build();
    }
}
