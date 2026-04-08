package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.HandleRepairRequest;
import com.dormitory.dto.request.RepairRequestDto;
import com.dormitory.dto.response.RepairResponse;
import com.dormitory.entity.Building;
import com.dormitory.entity.RepairRequest;
import com.dormitory.entity.Room;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RepairRequestMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.RepairService;
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
public class RepairServiceImpl implements RepairService {
    
    private final RepairRequestMapper repairMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    
    @Override
    @Transactional
    public RepairResponse create(RepairRequestDto request) {
        User student = userMapper.selectById(request.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        Room room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        Building building = buildingMapper.selectById(room.getBuildingId());
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        RepairRequest repair = new RepairRequest();
        repair.setStudentId(request.getStudentId());
        repair.setRoomId(request.getRoomId());
        repair.setRepairType(request.getRepairType());
        repair.setDescription(request.getDescription());
        repair.setImages(request.getImages());
        repair.setPriority(request.getPriority() != null ? request.getPriority() : "NORMAL");
        repair.setStatus("PENDING");
        
        repairMapper.insert(repair);
        
        return convertToResponse(repair, student, room, building, null);
    }
    
    @Override
    @Transactional
    public RepairResponse handle(Long id, HandleRepairRequest request) {
        RepairRequest repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        
        if ("COMPLETED".equals(repair.getStatus()) || "CANCELLED".equals(repair.getStatus())) {
            throw new BusinessException("该报修已处理完成或已取消");
        }
        
        User handler = null;
        if (request.getHandlerId() != null) {
            handler = userMapper.selectById(request.getHandlerId());
            repair.setHandlerId(request.getHandlerId());
        }
        
        if (request.getHandleResult() != null) {
            repair.setHandleResult(request.getHandleResult());
        }
        
        if (request.getRepairCost() != null) {
            repair.setRepairCost(request.getRepairCost());
        }
        
        if (request.getStatus() != null) {
            repair.setStatus(request.getStatus());
            if ("COMPLETED".equals(request.getStatus())) {
                repair.setHandleTime(LocalDateTime.now());
            }
        }
        
        repairMapper.updateById(repair);
        
        User student = userMapper.selectById(repair.getStudentId());
        Room room = roomMapper.selectById(repair.getRoomId());
        Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
        
        return convertToResponse(repair, student, room, building, handler);
    }
    
    @Override
    @Transactional
    public RepairResponse cancel(Long id) {
        RepairRequest repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        
        if (!"PENDING".equals(repair.getStatus())) {
            throw new BusinessException("只能取消待处理的报修");
        }
        
        repair.setStatus("CANCELLED");
        repairMapper.updateById(repair);
        
        User student = userMapper.selectById(repair.getStudentId());
        Room room = roomMapper.selectById(repair.getRoomId());
        Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
        
        return convertToResponse(repair, student, room, building, null);
    }
    
    @Override
    public RepairResponse getById(Long id) {
        RepairRequest repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        return buildResponse(repair);
    }
    
    @Override
    public List<RepairResponse> getByStudentId(Long studentId) {
        LambdaQueryWrapper<RepairRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairRequest::getStudentId, studentId);
        return repairMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RepairResponse> getByStatus(String status) {
        LambdaQueryWrapper<RepairRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairRequest::getStatus, status);
        return repairMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<RepairResponse> getPage(Integer page, Integer size, Long buildingId, String status, String keyword) {
        Page<RepairRequest> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<RepairRequest> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(RepairRequest::getStatus, status);
        }
        wrapper.orderByDesc(RepairRequest::getCreateTime);
        
        IPage<RepairRequest> pageResult = repairMapper.selectPage(mpPage, wrapper);
        
        List<RepairResponse> responses = pageResult.getRecords().stream()
                .map(this::buildResponse)
                .filter(res -> {
                    if (buildingId != null) {
                        Room r = roomMapper.selectById(res.getRoomId());
                        if (r == null || !buildingId.equals(r.getBuildingId())) {
                            return false;
                        }
                    }
                    if (StringUtils.hasText(keyword)) {
                        return (res.getStudentName() != null && res.getStudentName().contains(keyword)) ||
                               (res.getRoomNumber() != null && res.getRoomNumber().contains(keyword)) ||
                               (res.getRepairType() != null && res.getRepairType().contains(keyword));
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    private RepairResponse buildResponse(RepairRequest repair) {
        User student = userMapper.selectById(repair.getStudentId());
        Room room = roomMapper.selectById(repair.getRoomId());
        Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
        User handler = repair.getHandlerId() != null ? userMapper.selectById(repair.getHandlerId()) : null;
        
        return convertToResponse(repair, student, room, building, handler);
    }
    
    private RepairResponse convertToResponse(RepairRequest repair, User student, Room room, Building building, User handler) {
        return RepairResponse.builder()
                .id(repair.getId())
                .studentId(repair.getStudentId())
                .studentName(student != null ? student.getRealName() : null)
                .roomId(repair.getRoomId())
                .buildingId(room != null ? room.getBuildingId() : null)
                .roomNumber(room != null ? room.getRoomNumber() : null)
                .buildingName(building != null ? building.getBuildingName() : null)
                .repairType(repair.getRepairType())
                .description(repair.getDescription())
                .images(repair.getImages())
                .priority(repair.getPriority())
                .status(repair.getStatus())
                .handlerId(repair.getHandlerId())
                .handlerName(handler != null ? handler.getRealName() : null)
                .handleResult(repair.getHandleResult())
                .handleTime(repair.getHandleTime())
                .repairCost(repair.getRepairCost())
                .createTime(repair.getCreateTime())
                .updateTime(repair.getUpdateTime())
                .build();
    }
}
