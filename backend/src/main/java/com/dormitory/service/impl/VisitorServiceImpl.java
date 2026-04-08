package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.VisitorRequest;
import com.dormitory.dto.response.VisitorResponse;
import com.dormitory.entity.Building;
import com.dormitory.entity.Room;
import com.dormitory.entity.Visitor;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.VisitorMapper;
import com.dormitory.service.VisitorService;
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
public class VisitorServiceImpl implements VisitorService {
    
    private final VisitorMapper visitorMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    
    @Override
    @Transactional
    public VisitorResponse create(VisitorRequest request) {
        Visitor visitor = new Visitor();
        visitor.setVisitorName(request.getVisitorName());
        visitor.setVisitorPhone(request.getVisitorPhone());
        visitor.setIdCard(request.getIdCard());
        visitor.setVisitorType(request.getVisitorType());
        visitor.setVisitRoomId(request.getVisitRoomId());
        visitor.setVisitStudentId(request.getVisitStudentId());
        visitor.setVisitStudentName(request.getVisitStudentName());
        visitor.setVisitReason(request.getVisitReason());
        visitor.setVisitTime(request.getVisitTime());
        visitor.setRemark(request.getRemark());
        visitor.setRegister(request.getRegister());
        visitor.setStatus("VISITING");
        
        visitorMapper.insert(visitor);
        return buildResponse(visitor);
    }
    
    @Override
    @Transactional
    public VisitorResponse update(Long id, VisitorRequest request) {
        Visitor visitor = visitorMapper.selectById(id);
        if (visitor == null) {
            throw new BusinessException("访客记录不存在");
        }
        
        visitor.setVisitorName(request.getVisitorName());
        visitor.setVisitorPhone(request.getVisitorPhone());
        visitor.setIdCard(request.getIdCard());
        visitor.setVisitorType(request.getVisitorType());
        visitor.setVisitReason(request.getVisitReason());
        visitor.setRemark(request.getRemark());
        
        visitorMapper.updateById(visitor);
        return buildResponse(visitor);
    }
    
    @Override
    @Transactional
    public VisitorResponse leave(Long id) {
        Visitor visitor = visitorMapper.selectById(id);
        if (visitor == null) {
            throw new BusinessException("访客记录不存在");
        }
        
        if ("LEFT".equals(visitor.getStatus())) {
            throw new BusinessException("该访客已离开");
        }
        
        visitor.setStatus("LEFT");
        visitor.setLeaveTime(LocalDateTime.now());
        
        visitorMapper.updateById(visitor);
        return buildResponse(visitor);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Visitor visitor = visitorMapper.selectById(id);
        if (visitor == null) {
            throw new BusinessException("访客记录不存在");
        }
        visitorMapper.deleteById(id);
    }
    
    @Override
    public VisitorResponse getById(Long id) {
        Visitor visitor = visitorMapper.selectById(id);
        if (visitor == null) {
            throw new BusinessException("访客记录不存在");
        }
        return buildResponse(visitor);
    }
    
    @Override
    public List<VisitorResponse> getByRoomId(Long roomId) {
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Visitor::getVisitRoomId, roomId);
        return visitorMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<VisitorResponse> getVisiting() {
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Visitor::getStatus, "VISITING");
        return visitorMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<VisitorResponse> getPage(Integer page, Integer size, Long roomId, String status, String keyword) {
        Page<Visitor> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<>();
        if (roomId != null) {
            wrapper.eq(Visitor::getVisitRoomId, roomId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Visitor::getStatus, status);
        }
        wrapper.orderByDesc(Visitor::getCreateTime);
        
        IPage<Visitor> pageResult = visitorMapper.selectPage(mpPage, wrapper);
        
        List<VisitorResponse> responses = pageResult.getRecords().stream()
                .map(this::buildResponse)
                .filter(res -> {
                    if (StringUtils.hasText(keyword)) {
                        return (res.getVisitorName() != null && res.getVisitorName().contains(keyword)) ||
                               (res.getVisitStudentName() != null && res.getVisitStudentName().contains(keyword));
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    private VisitorResponse buildResponse(Visitor visitor) {
        Room room = visitor.getVisitRoomId() != null ? roomMapper.selectById(visitor.getVisitRoomId()) : null;
        Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
        
        return VisitorResponse.builder()
                .id(visitor.getId())
                .visitorName(visitor.getVisitorName())
                .visitorPhone(visitor.getVisitorPhone())
                .idCard(visitor.getIdCard())
                .visitorType(visitor.getVisitorType())
                .visitRoomId(visitor.getVisitRoomId())
                .roomNumber(room != null ? room.getRoomNumber() : null)
                .buildingName(building != null ? building.getBuildingName() : null)
                .visitStudentId(visitor.getVisitStudentId())
                .visitStudentName(visitor.getVisitStudentName())
                .visitReason(visitor.getVisitReason())
                .visitTime(visitor.getVisitTime())
                .leaveTime(visitor.getLeaveTime())
                .status(visitor.getStatus())
                .remark(visitor.getRemark())
                .register(visitor.getRegister())
                .createTime(visitor.getCreateTime())
                .updateTime(visitor.getUpdateTime())
                .build();
    }
}
