package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.annotation.DataScope;
import com.dormitory.aspect.DataScopeAspect;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.CheckInRequest;
import com.dormitory.dto.request.CheckOutRequest;
import com.dormitory.dto.request.TransferRequest;
import com.dormitory.dto.response.AccommodationResponse;
import com.dormitory.entity.Building;
import com.dormitory.entity.Room;
import com.dormitory.entity.StudentAccommodation;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.AccommodationMapper;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.StudentAccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAccommodationServiceImpl implements StudentAccommodationService {
    
    private final AccommodationMapper accommodationMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    
    @Override
    @Transactional
    public AccommodationResponse checkIn(CheckInRequest request) {
        User student = userMapper.selectById(request.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        if (!"STUDENT".equals(student.getUserType())) {
            throw new BusinessException("只能为学生办理入住");
        }
        
        LambdaQueryWrapper<StudentAccommodation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAccommodation::getStudentId, request.getStudentId())
                .eq(StudentAccommodation::getStatus, "ACTIVE");
        if (accommodationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该学生已有在住记录");
        }
        
        Room room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        if (room.getOccupiedBeds() >= room.getCapacity()) {
            throw new BusinessException("该房间已满");
        }
        
        if ("MAINTENANCE".equals(room.getStatus())) {
            throw new BusinessException("该房间正在维修中");
        }
        
        Building building = buildingMapper.selectById(room.getBuildingId());
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        StudentAccommodation accommodation = new StudentAccommodation();
        accommodation.setStudentId(request.getStudentId());
        accommodation.setRoomId(request.getRoomId());
        accommodation.setBuildingId(room.getBuildingId());
        accommodation.setBedNumber(request.getBedNumber());
        accommodation.setCheckInDate(request.getCheckInDate());
        accommodation.setExpectedCheckOutDate(request.getExpectedCheckOutDate());
        accommodation.setDeposit(request.getDeposit());
        accommodation.setStatus("ACTIVE");
        accommodation.setRemark(request.getRemark());
        
        accommodationMapper.insert(accommodation);
        
        room.setOccupiedBeds(room.getOccupiedBeds() + 1);
        if (room.getOccupiedBeds() >= room.getCapacity()) {
            room.setStatus("FULL");
        } else {
            room.setStatus("PARTIAL");
        }
        roomMapper.updateById(room);
        
        building.setAvailableBeds(building.getAvailableBeds() - 1);
        buildingMapper.updateById(building);
        
        return convertToResponse(accommodation, student, room, building);
    }
    
    @Override
    @Transactional
    public AccommodationResponse transfer(TransferRequest request) {
        User student = userMapper.selectById(request.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        StudentAccommodation oldAccommodation = findActiveByStudentId(request.getStudentId());
        if (oldAccommodation == null) {
            throw new BusinessException("该学生无在住记录");
        }
        
        if (!oldAccommodation.getRoomId().equals(request.getOldRoomId())) {
            throw new BusinessException("原房间信息不匹配");
        }
        
        Room newRoom = roomMapper.selectById(request.getNewRoomId());
        if (newRoom == null) {
            throw new BusinessException("新房间不存在");
        }
        
        if (newRoom.getOccupiedBeds() >= newRoom.getCapacity()) {
            throw new BusinessException("新房间已满");
        }
        
        if ("MAINTENANCE".equals(newRoom.getStatus())) {
            throw new BusinessException("新房间正在维修中");
        }
        
        Room oldRoom = roomMapper.selectById(request.getOldRoomId());
        if (oldRoom == null) {
            throw new BusinessException("原房间不存在");
        }
        
        Building newBuilding = buildingMapper.selectById(newRoom.getBuildingId());
        if (newBuilding == null) {
            throw new BusinessException("新楼栋不存在");
        }
        
        oldAccommodation.setStatus("TRANSFERRED");
        oldAccommodation.setActualCheckOutDate(request.getTransferDate());
        accommodationMapper.updateById(oldAccommodation);
        
        StudentAccommodation newAccommodation = new StudentAccommodation();
        newAccommodation.setStudentId(request.getStudentId());
        newAccommodation.setRoomId(request.getNewRoomId());
        newAccommodation.setBuildingId(newRoom.getBuildingId());
        newAccommodation.setBedNumber(request.getNewBedNumber());
        newAccommodation.setCheckInDate(request.getTransferDate());
        newAccommodation.setDeposit(oldAccommodation.getDeposit());
        newAccommodation.setStatus("ACTIVE");
        newAccommodation.setRemark(request.getReason());
        
        accommodationMapper.insert(newAccommodation);
        
        oldRoom.setOccupiedBeds(oldRoom.getOccupiedBeds() - 1);
        if (oldRoom.getOccupiedBeds() == 0) {
            oldRoom.setStatus("AVAILABLE");
        } else {
            oldRoom.setStatus("PARTIAL");
        }
        roomMapper.updateById(oldRoom);
        
        newRoom.setOccupiedBeds(newRoom.getOccupiedBeds() + 1);
        if (newRoom.getOccupiedBeds() >= newRoom.getCapacity()) {
            newRoom.setStatus("FULL");
        } else {
            newRoom.setStatus("PARTIAL");
        }
        roomMapper.updateById(newRoom);
        
        Building oldBuilding = buildingMapper.selectById(oldRoom.getBuildingId());
        if (oldBuilding != null) {
            oldBuilding.setAvailableBeds(oldBuilding.getAvailableBeds() + 1);
            buildingMapper.updateById(oldBuilding);
        }
        newBuilding.setAvailableBeds(newBuilding.getAvailableBeds() - 1);
        buildingMapper.updateById(newBuilding);
        
        return convertToResponse(newAccommodation, student, newRoom, newBuilding);
    }
    
    private StudentAccommodation findActiveByStudentId(Long studentId) {
        LambdaQueryWrapper<StudentAccommodation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAccommodation::getStudentId, studentId)
                .eq(StudentAccommodation::getStatus, "ACTIVE");
        List<StudentAccommodation> list = accommodationMapper.selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }
    
    @Override
    @Transactional
    public AccommodationResponse checkOut(CheckOutRequest request) {
        StudentAccommodation accommodation = accommodationMapper.selectById(request.getAccommodationId());
        if (accommodation == null) {
            throw new BusinessException("住宿记录不存在");
        }
        
        if (!"ACTIVE".equals(accommodation.getStatus())) {
            throw new BusinessException("该住宿记录已退宿");
        }
        
        User student = userMapper.selectById(accommodation.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        Room room = roomMapper.selectById(accommodation.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        Building building = buildingMapper.selectById(room.getBuildingId());
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        
        accommodation.setStatus("CHECKED_OUT");
        accommodation.setActualCheckOutDate(request.getCheckOutDate());
        accommodation.setRemark((accommodation.getRemark() != null ? accommodation.getRemark() : "") + " | 退宿原因：" + request.getReason());
        accommodationMapper.updateById(accommodation);
        
        room.setOccupiedBeds(room.getOccupiedBeds() - 1);
        if (room.getOccupiedBeds() == 0) {
            room.setStatus("AVAILABLE");
        } else {
            room.setStatus("PARTIAL");
        }
        roomMapper.updateById(room);
        
        building.setAvailableBeds(building.getAvailableBeds() + 1);
        buildingMapper.updateById(building);
        
        return convertToResponse(accommodation, student, room, building);
    }
    
    @Override
    public AccommodationResponse getById(Long id) {
        StudentAccommodation accommodation = accommodationMapper.selectById(id);
        if (accommodation == null) {
            throw new BusinessException("住宿记录不存在");
        }
        
        User student = userMapper.selectById(accommodation.getStudentId());
        Room room = roomMapper.selectById(accommodation.getRoomId());
        Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
        
        return convertToResponse(accommodation, student, room, building);
    }
    
    @Override
    public AccommodationResponse getCurrentByStudentId(Long studentId) {
        StudentAccommodation accommodation = findActiveByStudentId(studentId);
        if (accommodation == null) {
            throw new BusinessException("该学生无在住记录");
        }
        
        User student = userMapper.selectById(accommodation.getStudentId());
        Room room = roomMapper.selectById(accommodation.getRoomId());
        Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
        
        return convertToResponse(accommodation, student, room, building);
    }
    
    @Override
    public List<AccommodationResponse> getByRoomId(Long roomId) {
        LambdaQueryWrapper<StudentAccommodation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAccommodation::getRoomId, roomId);
        return accommodationMapper.selectList(wrapper).stream()
                .map(acc -> {
                    User student = userMapper.selectById(acc.getStudentId());
                    Room room = roomMapper.selectById(acc.getRoomId());
                    Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
                    return convertToResponse(acc, student, room, building);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AccommodationResponse> getByBuildingId(Long buildingId) {
        LambdaQueryWrapper<StudentAccommodation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAccommodation::getBuildingId, buildingId);
        return accommodationMapper.selectList(wrapper).stream()
                .map(acc -> {
                    User student = userMapper.selectById(acc.getStudentId());
                    Room room = roomMapper.selectById(acc.getRoomId());
                    Building building = buildingMapper.selectById(acc.getBuildingId());
                    return convertToResponse(acc, student, room, building);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @DataScope(value = DataScope.DataScopeType.SELF, permission = "dorm:accommodation:all")
    public PageResult<AccommodationResponse> getPage(Integer page, Integer size, Long buildingId, Long roomId, String keyword) {
        Page<StudentAccommodation> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<StudentAccommodation> wrapper = new LambdaQueryWrapper<>();
        
        // 应用数据权限过滤
        Long currentUserId = DataScopeAspect.DataScopeContext.getCurrentUserId();
        List<Long> buildingIds = DataScopeAspect.DataScopeContext.getCurrentUserBuildingIds();
        
        if (currentUserId != null) {
            // 学生只能查看自己的住宿记录
            log.info("应用本人数据权限过滤: student_id = {}", currentUserId);
            wrapper.eq(StudentAccommodation::getStudentId, currentUserId);
        } else if (buildingIds != null && !buildingIds.isEmpty()) {
            // 宿管只能查看自己管理的楼栋的住宿记录
            log.info("应用楼栋数据权限过滤: building_ids = {}", buildingIds);
            wrapper.in(StudentAccommodation::getBuildingId, buildingIds);
        }
        
        if (buildingId != null) {
            wrapper.eq(StudentAccommodation::getBuildingId, buildingId);
        }
        if (roomId != null) {
            wrapper.eq(StudentAccommodation::getRoomId, roomId);
        }
        wrapper.orderByDesc(StudentAccommodation::getCreateTime);
        
        IPage<StudentAccommodation> pageResult = accommodationMapper.selectPage(mpPage, wrapper);
        
        List<AccommodationResponse> responses = pageResult.getRecords().stream()
                .map(acc -> {
                    User student = userMapper.selectById(acc.getStudentId());
                    Room room = roomMapper.selectById(acc.getRoomId());
                    Building building = room != null ? buildingMapper.selectById(room.getBuildingId()) : null;
                    return convertToResponse(acc, student, room, building);
                })
                .filter(res -> {
                    if (StringUtils.hasText(keyword)) {
                        return (res.getStudentName() != null && res.getStudentName().contains(keyword)) ||
                               (res.getStudentIdNumber() != null && res.getStudentIdNumber().contains(keyword)) ||
                               (res.getRoomNumber() != null && res.getRoomNumber().contains(keyword));
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    @Override
    public List<AccommodationResponse> getAvailableBeds(Long buildingId) {
        List<Room> rooms;
        if (buildingId != null) {
            LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Room::getBuildingId, buildingId);
            rooms = roomMapper.selectList(wrapper);
        } else {
            rooms = roomMapper.selectList(null);
        }
        
        List<AccommodationResponse> availableBeds = new ArrayList<>();
        for (Room room : rooms) {
            if (!"MAINTENANCE".equals(room.getStatus()) && room.getOccupiedBeds() < room.getCapacity()) {
                Building building = buildingMapper.selectById(room.getBuildingId());
                int available = room.getCapacity() - room.getOccupiedBeds();
                
                for (int i = 1; i <= available; i++) {
                    availableBeds.add(AccommodationResponse.builder()
                            .roomId(room.getId())
                            .roomNumber(room.getRoomNumber())
                            .buildingId(room.getBuildingId())
                            .buildingName(building != null ? building.getBuildingName() : null)
                            .bedNumber(String.valueOf(i))
                            .build());
                }
            }
        }
        
        return availableBeds;
    }
    
    private AccommodationResponse convertToResponse(StudentAccommodation acc, User student, Room room, Building building) {
        return AccommodationResponse.builder()
                .id(acc.getId())
                .studentId(acc.getStudentId())
                .studentName(student != null ? student.getRealName() : null)
                .studentIdNumber(student != null ? student.getStudentId() : null)
                .roomId(acc.getRoomId())
                .roomNumber(room != null ? room.getRoomNumber() : null)
                .buildingId(acc.getBuildingId())
                .buildingName(building != null ? building.getBuildingName() : null)
                .bedNumber(acc.getBedNumber())
                .checkInDate(acc.getCheckInDate())
                .expectedCheckOutDate(acc.getExpectedCheckOutDate())
                .actualCheckOutDate(acc.getActualCheckOutDate())
                .status(acc.getStatus())
                .deposit(acc.getDeposit())
                .remark(acc.getRemark())
                .createTime(acc.getCreateTime())
                .updateTime(acc.getUpdateTime())
                .build();
    }
}
