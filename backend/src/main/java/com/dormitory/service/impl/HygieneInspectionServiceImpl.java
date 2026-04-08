package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.HygieneInspectionRequest;
import com.dormitory.dto.response.HygieneInspectionResponse;
import com.dormitory.entity.Building;
import com.dormitory.entity.HygieneInspection;
import com.dormitory.entity.Room;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.HygieneInspectionMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.service.HygieneInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HygieneInspectionServiceImpl implements HygieneInspectionService {
    
    private final HygieneInspectionMapper hygieneMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    
    @Override
    @Transactional
    public HygieneInspectionResponse create(HygieneInspectionRequest request) {
        Room room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        Building building = buildingMapper.selectById(room.getBuildingId());
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        HygieneInspection inspection = new HygieneInspection();
        inspection.setRoomId(request.getRoomId());
        inspection.setBuildingId(room.getBuildingId());
        inspection.setInspectionDate(request.getInspectionDate());
        inspection.setScore(request.getScore());
        inspection.setItems(request.getItems());
        inspection.setProblems(request.getProblems());
        inspection.setImages(request.getImages());
        inspection.setInspectorId(request.getInspectorId());
        inspection.setInspectorName(request.getInspectorName());
        inspection.setRemark(request.getRemark());
        
        inspection.setGrade(calculateGrade(request.getScore()));
        
        hygieneMapper.insert(inspection);
        return convertToResponse(inspection, room, building);
    }
    
    @Override
    @Transactional
    public HygieneInspectionResponse update(Long id, HygieneInspectionRequest request) {
        HygieneInspection inspection = hygieneMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException("检查记录不存在");
        }
        
        Room room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        Building building = buildingMapper.selectById(room.getBuildingId());
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        inspection.setRoomId(request.getRoomId());
        inspection.setBuildingId(room.getBuildingId());
        inspection.setInspectionDate(request.getInspectionDate());
        inspection.setScore(request.getScore());
        inspection.setItems(request.getItems());
        inspection.setProblems(request.getProblems());
        inspection.setImages(request.getImages());
        inspection.setInspectorId(request.getInspectorId());
        inspection.setInspectorName(request.getInspectorName());
        inspection.setRemark(request.getRemark());
        inspection.setGrade(calculateGrade(request.getScore()));
        
        hygieneMapper.updateById(inspection);
        return convertToResponse(inspection, room, building);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        HygieneInspection inspection = hygieneMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException("检查记录不存在");
        }
        hygieneMapper.deleteById(id);
    }
    
    @Override
    public HygieneInspectionResponse getById(Long id) {
        HygieneInspection inspection = hygieneMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException("检查记录不存在");
        }
        return buildResponse(inspection);
    }
    
    @Override
    public List<HygieneInspectionResponse> getByRoomId(Long roomId) {
        LambdaQueryWrapper<HygieneInspection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HygieneInspection::getRoomId, roomId);
        return hygieneMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<HygieneInspectionResponse> getByBuildingId(Long buildingId) {
        LambdaQueryWrapper<HygieneInspection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HygieneInspection::getBuildingId, buildingId);
        return hygieneMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<HygieneInspectionResponse> getPage(Integer page, Integer size, Long buildingId, Long roomId, String grade) {
        Page<HygieneInspection> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<HygieneInspection> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(HygieneInspection::getBuildingId, buildingId);
        }
        if (roomId != null) {
            wrapper.eq(HygieneInspection::getRoomId, roomId);
        }
        if (StringUtils.hasText(grade)) {
            wrapper.eq(HygieneInspection::getGrade, grade);
        }
        wrapper.orderByDesc(HygieneInspection::getCreateTime);
        
        IPage<HygieneInspection> pageResult = hygieneMapper.selectPage(mpPage, wrapper);
        
        List<HygieneInspectionResponse> responses = pageResult.getRecords().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    private String calculateGrade(Integer score) {
        if (score >= 90) {
            return "EXCELLENT";
        } else if (score >= 80) {
            return "GOOD";
        } else if (score >= 60) {
            return "QUALIFIED";
        } else {
            return "UNQUALIFIED";
        }
    }
    
    private HygieneInspectionResponse buildResponse(HygieneInspection inspection) {
        Room room = roomMapper.selectById(inspection.getRoomId());
        Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
        return convertToResponse(inspection, room, building);
    }
    
    private HygieneInspectionResponse convertToResponse(HygieneInspection inspection, Room room, Building building) {
        return HygieneInspectionResponse.builder()
                .id(inspection.getId())
                .roomId(inspection.getRoomId())
                .roomNumber(room != null ? room.getRoomNumber() : null)
                .buildingName(building != null ? building.getBuildingName() : null)
                .inspectionDate(inspection.getInspectionDate())
                .score(inspection.getScore())
                .grade(inspection.getGrade())
                .items(inspection.getItems())
                .problems(inspection.getProblems())
                .images(inspection.getImages())
                .inspectorId(inspection.getInspectorId())
                .inspectorName(inspection.getInspectorName())
                .remark(inspection.getRemark())
                .createTime(inspection.getCreateTime())
                .updateTime(inspection.getUpdateTime())
                .build();
    }
}
