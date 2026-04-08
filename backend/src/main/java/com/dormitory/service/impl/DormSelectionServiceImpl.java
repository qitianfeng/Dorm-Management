package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.DormSelectionRequest;
import com.dormitory.entity.*;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.*;
import com.dormitory.service.DormSelectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DormSelectionServiceImpl implements DormSelectionService {
    
    private final DormSelectionConfigMapper configMapper;
    private final DormSelectionRecordMapper recordMapper;
    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;
    private final AccommodationMapper accommodationMapper;
    
    @Override
    public DormSelectionConfig getActiveConfig() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<DormSelectionConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormSelectionConfig::getStatus, "ACTIVE")
                .le(DormSelectionConfig::getStartTime, now)
                .ge(DormSelectionConfig::getEndTime, now)
                .orderByDesc(DormSelectionConfig::getCreateTime)
                .last("LIMIT 1");
        return configMapper.selectOne(wrapper);
    }
    
    /**
     * 验证学生是否有资格参与选宿舍
     */
    private void validateStudentEligibility(DormSelectionConfig config, User student) {
        // 1. 检查时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(config.getStartTime())) {
            throw new BusinessException("选宿舍尚未开始");
        }
        if (now.isAfter(config.getEndTime())) {
            throw new BusinessException("选宿舍已经结束");
        }
        
        // 2. 检查性别限制
        if (config.getGenderRestriction() != null && !"ALL".equals(config.getGenderRestriction())) {
            String studentGender = student.getGender();
            if (studentGender == null || studentGender.isEmpty()) {
                throw new BusinessException("学生性别信息不完整");
            }
            if (!"ALL".equals(config.getGenderRestriction()) && 
                !config.getGenderRestriction().equals(studentGender)) {
                throw new BusinessException("该选宿舍配置仅限" + config.getGenderRestriction() + "性别学生");
            }
        }
        
        // 3. 检查学院限制
        if (config.getAllowedColleges() != null && !config.getAllowedColleges().isEmpty()) {
            String studentCollege = student.getDepartment();
            List<String> allowedColleges = Arrays.asList(config.getAllowedColleges().split(","));
            if (studentCollege == null || studentCollege.isEmpty()) {
                throw new BusinessException("学生学院信息不完整");
            }
            if (!allowedColleges.contains(studentCollege.trim())) {
                throw new BusinessException("您的学院不在允许选宿舍的学院范围内");
            }
        }
        
        // 4. 检查年级限制
        if (config.getAllowedGrades() != null && !config.getAllowedGrades().isEmpty()) {
            String studentGrade = extractGrade(student.getStudentId());
            if (studentGrade == null) {
                throw new BusinessException("无法从学号提取年级信息");
            }
            List<String> allowedGrades = Arrays.asList(config.getAllowedGrades().split(","));
            if (!allowedGrades.contains(studentGrade)) {
                throw new BusinessException("您的年级不在允许选宿舍的年级范围内");
            }
        }
    }
    
    /**
     * 从学号提取年级（如2024001 -> 2024级）
     */
    private String extractGrade(String studentId) {
        if (studentId == null || studentId.length() < 5) {
            return null;
        }
        // 学号格式：2024001 -> 提取年级
        String yearPart = studentId.substring(0, 4);
        if (yearPart.matches("\\d{4}")) {
            int year = Integer.parseInt(yearPart);
            int currentYear = LocalDateTime.now().getYear();
            return String.valueOf(currentYear - year + 1);
        }
        return studentId.substring(0, 4); // 返回原始学号
    }
    
    /**
     * 检查学生是否有有效的住宿记录（已入住状态）
     */
    private boolean hasActiveAccommodation(Long studentId) {
        if (studentId == null) {
            return false;
        }
        LambdaQueryWrapper<StudentAccommodation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAccommodation::getStudentId, studentId)
                .eq(StudentAccommodation::getStatus, "ACTIVE");
        return accommodationMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public List<Object> getAvailableRooms(Long configId, Long buildingId) {
        DormSelectionConfig config = configMapper.selectById(configId);
        if (config == null) {
            throw new BusinessException("选宿舍配置不存在");
        }
        
        // 获取配置中允许的楼栋
        List<Long> buildingIds = parseBuildingIds(config.getBuildingIds());
        if (buildingId != null && !buildingIds.contains(buildingId)) {
            throw new BusinessException("该楼栋不在可选范围内");
        }
        
        // 检查楼栋性别限制
        if (buildingId != null && config.getGenderRestriction() != null && !"All".equals(config.getGenderRestriction())) {
            Building building = buildingMapper.selectById(buildingId);
            if (building != null && building.getGenderType() != null && 
                !building.getGenderType().equals(config.getGenderRestriction())) {
                throw new BusinessException("该楼栋性别限制为" + config.getGenderRestriction());
            }
        }
        
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(Room::getBuildingId, buildingId);
        } else if (!buildingIds.isEmpty()) {
            wrapper.in(Room::getBuildingId, buildingIds);
        }
        wrapper.eq(Room::getStatus, "AVAILABLE");
        
        List<Room> rooms = roomMapper.selectList(wrapper);
        
        // 过滤出有空位的房间
        rooms = rooms.stream()
                .filter(r -> r.getOccupiedBeds() < r.getCapacity())
                .collect(Collectors.toList());
        
        // 获取楼栋信息
        List<Long> roomBuildingIds = rooms.stream().map(Room::getBuildingId).distinct().collect(Collectors.toList());
        Map<Long, Building> buildingMap = new HashMap<>();
        if (!roomBuildingIds.isEmpty()) {
            buildingMapper.selectBatchIds(roomBuildingIds).forEach(b -> buildingMap.put(b.getId(), b));
        }
        
        // 获取已选床位（使用悲观锁)
        LambdaQueryWrapper<DormSelectionRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(DormSelectionRecord::getConfigId, configId)
                .eq(DormSelectionRecord::getStatus, "SELECTED");
        List<DormSelectionRecord> records = recordMapper.selectList(recordWrapper);
        
        Map<Long, Set<String>> selectedBeds = records.stream()
                .collect(Collectors.groupingBy(
                        DormSelectionRecord::getRoomId,
                        Collectors.mapping(DormSelectionRecord::getBedNumber, Collectors.toSet())
                ));
        
        return rooms.stream().map(room -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", room.getId());
            result.put("roomNumber", room.getRoomNumber());
            result.put("buildingId", room.getBuildingId());
            result.put("buildingName", buildingMap.get(room.getBuildingId()) != null ? 
                    buildingMap.get(room.getBuildingId()).getBuildingName() : "");
            result.put("floor", room.getFloor());
            result.put("capacity", room.getCapacity());
            result.put("occupiedBeds", room.getOccupiedBeds());
            result.put("availableBeds", room.getCapacity() - room.getOccupiedBeds());
            result.put("roomType", room.getRoomType());
            result.put("facilities", room.getFacilities());
            
            // 可选床位
            Set<String> selected = selectedBeds.getOrDefault(room.getId(), Collections.emptySet());
            List<String> availableBeds = new ArrayList<>();
            for (int i = 0; i < room.getCapacity(); i++) {
                String bed = String.valueOf((char) ('A' + i));
                if (!selected.contains(bed)) {
                    availableBeds.add(bed);
                }
            }
            result.put("beds", availableBeds);
            
            return result;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public synchronized DormSelectionRecord selectRoom(Long studentId, DormSelectionRequest request) {
        // 使用 synchronized 块确保并发安全
        synchronized (this) {
            DormSelectionConfig config = getActiveConfig();
            if (config == null) {
                throw new BusinessException("当前没有可用的选宿舍配置");
            }
            
            // 获取学生信息
            User student = userMapper.selectById(studentId);
            if (student == null) {
                throw new BusinessException("学生不存在");
            }
            
            // 验证学生资格
            validateStudentEligibility(config, student);
            
            // 检查学生是否已有住宿记录（已入住学生不能选宿舍，应走换宿舍流程）
            if (hasActiveAccommodation(studentId)) {
                throw new BusinessException("您已有宿舍，不能重复选宿舍。如需更换宿舍，请申请换宿。");
            }
            
            // 检查是否已选择（使用悲观锁)
            DormSelectionRecord existing = getStudentSelection(studentId);
            if (existing != null) {
                throw new BusinessException("您已经选择过宿舍");
            }
            
            Room room = roomMapper.selectById(request.getRoomId());
            if (room == null) {
                throw new BusinessException("房间不存在");
            }
            
            // 风险检查：再次确认床位是否可用
            LambdaQueryWrapper<DormSelectionRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DormSelectionRecord::getConfigId, config.getId())
                    .eq(DormSelectionRecord::getRoomId, request.getRoomId())
                    .eq(DormSelectionRecord::getBedNumber, request.getBedNumber())
                    .eq(DormSelectionRecord::getStatus, "SELECTED");
            if (recordMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("该床位已被选择，请重新选择");
            }
            
            Building building = buildingMapper.selectById(room.getBuildingId());
            
            DormSelectionRecord record = new DormSelectionRecord();
            record.setConfigId(config.getId());
            record.setStudentId(studentId);
            record.setStudentName(student.getRealName());
            record.setBuildingId(room.getBuildingId());
            record.setBuildingName(building != null ? building.getBuildingName() : "");
            record.setRoomId(room.getId());
            record.setRoomNumber(room.getRoomNumber());
            record.setBedNumber(request.getBedNumber());
            record.setPriority(0);
            record.setStatus("SELECTED");
            record.setRemark(request.getRemark());
            record.setSelectTime(LocalDateTime.now());
            
            recordMapper.insert(record);
            
            // 更新房间已住人数
            room.setOccupiedBeds(room.getOccupiedBeds() + 1);
            if (room.getOccupiedBeds() >= room.getCapacity()) {
                room.setStatus("FULL");
            }
            roomMapper.updateById(room);
            
            return record;
        }
    }
    
    @Override
    public DormSelectionRecord getStudentSelection(Long studentId) {
        DormSelectionConfig config = getActiveConfig();
        if (config == null) {
            return null;
        }
        
        LambdaQueryWrapper<DormSelectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormSelectionRecord::getConfigId, config.getId())
                .eq(DormSelectionRecord::getStudentId, studentId)
                .eq(DormSelectionRecord::getStatus, "SELECTED");
        return recordMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional
    public synchronized void cancelSelection(Long studentId) {
        DormSelectionRecord record = getStudentSelection(studentId);
        if (record == null) {
            throw new BusinessException("未找到选宿舍记录");
        }
        
        // 检查学生是否已入住，已入住学生不能取消选宿舍
        if (hasActiveAccommodation(studentId)) {
            throw new BusinessException("您已入住宿舍，不能取消选宿舍。如需更换宿舍，请申请换宿；如需退宿，请申请退宿。");
        }
        
        record.setStatus("CANCELLED");
        recordMapper.updateById(record);
        
        // 更新房间已住人数
        Room room = roomMapper.selectById(record.getRoomId());
        if (room != null) {
            room.setOccupiedBeds(Math.max(0, room.getOccupiedBeds() - 1));
            if (room.getStatus().equals("FULL")) {
                room.setStatus("AVAILABLE");
            }
            roomMapper.updateById(room);
        }
    }
    
    @Override
    public PageResult<DormSelectionRecord> getSelectionRecords(Integer page, Integer size, Long configId, Long buildingId, String keyword) {
        Page<DormSelectionRecord> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<DormSelectionRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (configId != null) {
            wrapper.eq(DormSelectionRecord::getConfigId, configId);
        }
        if (buildingId != null) {
            wrapper.eq(DormSelectionRecord::getBuildingId, buildingId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(DormSelectionRecord::getStudentName, keyword)
                    .or()
                    .like(DormSelectionRecord::getRoomNumber, keyword)
            );
        }
        
        wrapper.orderByDesc(DormSelectionRecord::getSelectTime);
        
        IPage<DormSelectionRecord> pageResult = recordMapper.selectPage(mpPage, wrapper);
        
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), (long) size, (long) page);
    }
    
    @Override
    public DormSelectionRecord autoAllocate(Long studentId) {
        // 检查学生是否已有选宿舍记录（只检查活跃状态的记录）
        DormSelectionRecord existing = recordMapper.selectOne(
            new LambdaQueryWrapper<DormSelectionRecord>()
                .eq(DormSelectionRecord::getStudentId, studentId)
                .in(DormSelectionRecord::getStatus, "SELECTED", "AUTO_ALLOCATED")
        );
        
        if (existing != null) {
            return existing; // 已有选择，返回现有记录
        }
        
        // 获取学生信息
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 获取当前选宿舍配置
        DormSelectionConfig config = getActiveConfig();
        if (config == null) {
            throw new BusinessException("当前没有可用的选宿舍配置");
        }
        
        // 检查是否在时间范围内（允许结束后短时间内自动分配）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = config.getEndTime();
        // 允许结束后1小时内自动分配
        if (now.isAfter(endTime.plusHours(1))) {
            throw new BusinessException("选宿舍时间已结束，无法自动分配");
        }
        
        // 获取允许的楼栋列表
        List<Long> buildingIds = parseBuildingIds(config.getBuildingIds());
        if (buildingIds.isEmpty()) {
            throw new BusinessException("没有可分配的楼栋");
        }
        
        // 获取可选房间（按楼栋过滤）
        List<Object> availableRooms = getAvailableRooms(config.getId(), null);
        
        // 过滤符合学生条件的房间
        List<Map<String, Object>> eligibleRooms = new ArrayList<>();
        for (Object obj : availableRooms) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> room = (Map<String, Object>) obj;
                Long buildingId = toLong(room.get("buildingId"));
                Integer availableBeds = toInteger(room.get("availableBeds"));
                
                if (buildingIds.contains(buildingId) && availableBeds != null && availableBeds > 0) {
                    // 检查性别限制
                    String genderRestriction = config.getGenderRestriction();
                    if ("ALL".equals(genderRestriction) || genderRestriction == null) {
                        eligibleRooms.add(room);
                    }
                }
            }
        }
        
        if (eligibleRooms.isEmpty()) {
            throw new BusinessException("没有可分配的宿舍");
        }
        
        // 随机选择一个房间
        Map<String, Object> selectedRoom = eligibleRooms.get((int) (Math.random() * eligibleRooms.size()));
        
        // 获取床位
        @SuppressWarnings("unchecked")
        List<String> beds = (List<String>) selectedRoom.get("beds");
        if (beds == null || beds.isEmpty()) {
            throw new BusinessException("该房间没有可用床位");
        }
        String bedNumber = beds.get(0);
        
        Long roomId = toLong(selectedRoom.get("id"));
        Long buildingId = toLong(selectedRoom.get("buildingId"));
        String buildingName = (String) selectedRoom.get("buildingName");
        String roomNumber = (String) selectedRoom.get("roomNumber");
        
        // 创建选宿舍记录
        DormSelectionRecord record = new DormSelectionRecord();
        record.setConfigId(config.getId());
        record.setStudentId(studentId);
        record.setStudentName(student.getRealName());
        record.setBuildingId(buildingId);
        record.setBuildingName(buildingName);
        record.setRoomId(roomId);
        record.setRoomNumber(roomNumber);
        record.setBedNumber(bedNumber);
        record.setStatus("AUTO_ALLOCATED"); // 自动分配状态
        record.setSelectTime(now);
        
        recordMapper.insert(record);
        
        // 更新房间占用床位数
        Room room = roomMapper.selectById(roomId);
        if (room != null) {
            room.setOccupiedBeds(room.getOccupiedBeds() + 1);
            roomMapper.updateById(room);
        }
        
        log.info("自动分配宿舍成功: studentId={}, building={}, room={}, bed={}", 
                studentId, buildingName, roomNumber, bedNumber);
        
        return record;
    }
    
    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof String) return Long.parseLong((String) obj);
        return null;
    }
    
    private Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Long) return ((Long) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        return null;
    }
    
    private List<Long> parseBuildingIds(String buildingIds) {
        if (buildingIds == null || buildingIds.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(buildingIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
