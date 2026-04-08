package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.BuildingRequest;
import com.dormitory.dto.request.RoomRequest;
import com.dormitory.dto.response.BuildingResponse;
import com.dormitory.dto.response.RoomResponse;
import com.dormitory.entity.Building;
import com.dormitory.entity.Room;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.service.BuildingService;
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
public class BuildingServiceImpl implements BuildingService {
    
    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    
    @Override
    @Transactional
    public BuildingResponse createBuilding(BuildingRequest request) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Building::getBuildingCode, request.getBuildingCode());
        if (buildingMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("楼栋编号已存在");
        }
        
        Building building = new Building();
        building.setBuildingCode(request.getBuildingCode());
        building.setBuildingName(request.getBuildingName());
        building.setBuildingType(request.getBuildingType());
        building.setTotalFloors(request.getTotalFloors());
        building.setTotalRooms(request.getTotalRooms());
        building.setTotalBeds(request.getTotalBeds());
        building.setAvailableBeds(request.getTotalBeds());
        building.setLocation(request.getLocation());
        building.setManagerName(request.getManagerName());
        building.setManagerPhone(request.getManagerPhone());
        building.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        building.setRemark(request.getRemark());
        
        buildingMapper.insert(building);
        return convertToBuildingResponse(building);
    }
    
    @Override
    @Transactional
    public BuildingResponse updateBuilding(Long id, BuildingRequest request) {
        Building building = buildingMapper.selectById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Building::getBuildingCode, request.getBuildingCode());
        List<Building> existing = buildingMapper.selectList(wrapper);
        for (Building b : existing) {
            if (!b.getId().equals(id)) {
                throw new BusinessException("楼栋编号已存在");
            }
        }
        
        building.setBuildingCode(request.getBuildingCode());
        building.setBuildingName(request.getBuildingName());
        building.setBuildingType(request.getBuildingType());
        building.setTotalFloors(request.getTotalFloors());
        building.setTotalRooms(request.getTotalRooms());
        building.setTotalBeds(request.getTotalBeds());
        building.setLocation(request.getLocation());
        building.setManagerName(request.getManagerName());
        building.setManagerPhone(request.getManagerPhone());
        if (request.getStatus() != null) {
            building.setStatus(request.getStatus());
        }
        building.setRemark(request.getRemark());
        
        buildingMapper.updateById(building);
        return convertToBuildingResponse(building);
    }
    
    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        Building building = buildingMapper.selectById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getBuildingId, id);
        wrapper.ne(Room::getStatus, "MAINTENANCE");
        if (roomMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该楼栋下还有房间，无法删除");
        }
        
        buildingMapper.deleteById(id);
    }
    
    @Override
    public BuildingResponse getBuildingById(Long id) {
        Building building = buildingMapper.selectById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        return convertToBuildingResponse(building);
    }
    
    @Override
    public List<BuildingResponse> getAllBuildings() {
        return buildingMapper.selectList(null).stream()
                .map(this::convertToBuildingResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<BuildingResponse> getBuildingsPage(Integer page, Integer size, String keyword) {
        Page<Building> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Building::getBuildingCode, keyword)
                    .or()
                    .like(Building::getBuildingName, keyword)
                    .or()
                    .like(Building::getLocation, keyword);
        }
        wrapper.orderByDesc(Building::getCreateTime);
        
        IPage<Building> pageResult = buildingMapper.selectPage(mpPage, wrapper);
        
        List<BuildingResponse> responses = pageResult.getRecords().stream()
                .map(this::convertToBuildingResponse)
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Building building = buildingMapper.selectById(request.getBuildingId());
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getBuildingId, request.getBuildingId())
                .eq(Room::getRoomNumber, request.getRoomNumber());
        if (roomMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该楼栋下房间号已存在");
        }
        
        Room room = new Room();
        room.setBuildingId(request.getBuildingId());
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setOccupiedBeds(0);
        room.setRoomType(request.getRoomType() != null ? request.getRoomType() : "FOUR_PERSON");
        room.setStatus(request.getStatus() != null ? request.getStatus() : "AVAILABLE");
        room.setFacilities(request.getFacilities());
        room.setMonthlyFee(request.getMonthlyFee() != null ? request.getMonthlyFee().doubleValue() : 0.0);
        room.setRemark(request.getRemark());
        
        roomMapper.insert(room);
        return convertToRoomResponse(room, building);
    }
    
    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        Building building = buildingMapper.selectById(request.getBuildingId());
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getBuildingId, request.getBuildingId())
                .eq(Room::getRoomNumber, request.getRoomNumber());
        List<Room> existing = roomMapper.selectList(wrapper);
        for (Room r : existing) {
            if (!r.getId().equals(id)) {
                throw new BusinessException("该楼栋下房间号已存在");
            }
        }
        
        room.setBuildingId(request.getBuildingId());
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        if (request.getRoomType() != null) {
            room.setRoomType(request.getRoomType());
        }
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        room.setFacilities(request.getFacilities());
        room.setMonthlyFee(request.getMonthlyFee() != null ? request.getMonthlyFee().doubleValue() : 0.0);
        room.setRemark(request.getRemark());
        
        roomMapper.updateById(room);
        return convertToRoomResponse(room, building);
    }
    
    @Override
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        if (room.getOccupiedBeds() != null && room.getOccupiedBeds() > 0) {
            throw new BusinessException("该房间还有学生入住，无法删除");
        }
        
        roomMapper.deleteById(id);
    }
    
    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        Building building = buildingMapper.selectById(room.getBuildingId());
        return convertToRoomResponse(room, building);
    }
    
    @Override
    public List<RoomResponse> getRoomsByBuildingId(Long buildingId) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getBuildingId, buildingId);
        return roomMapper.selectList(wrapper).stream()
                .map(room -> {
                    Building building = buildingMapper.selectById(room.getBuildingId());
                    return convertToRoomResponse(room, building);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<RoomResponse> getRoomsPage(Integer page, Integer size, Long buildingId, String keyword) {
        Page<Room> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(Room::getBuildingId, buildingId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Room::getRoomNumber, keyword);
        }
        wrapper.orderByDesc(Room::getCreateTime);
        
        IPage<Room> pageResult = roomMapper.selectPage(mpPage, wrapper);
        
        List<RoomResponse> responses = pageResult.getRecords().stream()
                .map(room -> {
                    Building building = buildingMapper.selectById(room.getBuildingId());
                    return convertToRoomResponse(room, building);
                })
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    private BuildingResponse convertToBuildingResponse(Building building) {
        return BuildingResponse.builder()
                .id(building.getId())
                .buildingCode(building.getBuildingCode())
                .buildingName(building.getBuildingName())
                .buildingType(building.getBuildingType())
                .totalFloors(building.getTotalFloors())
                .totalRooms(building.getTotalRooms())
                .totalBeds(building.getTotalBeds())
                .availableBeds(building.getAvailableBeds())
                .location(building.getLocation())
                .managerName(building.getManagerName())
                .managerPhone(building.getManagerPhone())
                .status(building.getStatus())
                .remark(building.getRemark())
                .createTime(building.getCreateTime())
                .updateTime(building.getUpdateTime())
                .build();
    }
    
    private RoomResponse convertToRoomResponse(Room room, Building building) {
        return RoomResponse.builder()
                .id(room.getId())
                .buildingId(room.getBuildingId())
                .buildingName(building != null ? building.getBuildingName() : null)
                .roomNumber(room.getRoomNumber())
                .floor(room.getFloor())
                .capacity(room.getCapacity())
                .occupiedBeds(room.getOccupiedBeds())
                .availableBeds(room.getCapacity() - (room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0))
                .roomType(room.getRoomType())
                .status(room.getStatus())
                .facilities(room.getFacilities())
                .monthlyFee(room.getMonthlyFee() != null ? java.math.BigDecimal.valueOf(room.getMonthlyFee()) : null)
                .remark(room.getRemark())
                .createTime(room.getCreateTime())
                .updateTime(room.getUpdateTime())
                .build();
    }
}
