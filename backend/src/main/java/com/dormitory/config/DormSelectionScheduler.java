package com.dormitory.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.entity.DormSelectionConfig;
import com.dormitory.entity.DormSelectionRecord;
import com.dormitory.entity.User;
import com.dormitory.entity.Room;
import com.dormitory.entity.Building;
import com.dormitory.mapper.DormSelectionConfigMapper;
import com.dormitory.mapper.DormSelectionRecordMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.BuildingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 选宿舍定时任务
 * 用于在选宿舍时间结束后自动分配宿舍
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DormSelectionScheduler {
    
    private final DormSelectionConfigMapper configMapper;
    private final DormSelectionRecordMapper recordMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    
    /**
     * 每分钟检查一次是否需要执行自动分配
     * 在选宿舍结束后的1小时内，持续尝试为未选宿舍的学生分配
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    @Transactional
    public void autoAllocateExpiredSelections() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查询已结束但还在1小时缓冲期内的配置
        LambdaQueryWrapper<DormSelectionConfig> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(DormSelectionConfig::getStatus, "ACTIVE");
        List<DormSelectionConfig> configs = configMapper.selectList(configWrapper);
        
        for (DormSelectionConfig config : configs) {
            LocalDateTime endTime = config.getEndTime();
            
            // 检查是否已结束且在缓冲期内（结束后1小时内）
            if (now.isAfter(endTime) && now.isBefore(endTime.plusHours(1))) {
                log.info("检测到选宿舍时间已结束，开始自动分配: configId={}, 结束时间={}", config.getId(), endTime);
                allocateForConfig(config);
            }
        }
    }
    
    private void allocateForConfig(DormSelectionConfig config) {
        // 解析允许的楼栋
        List<Long> allowedBuildingIds = parseBuildingIds(config.getBuildingIds());
        if (allowedBuildingIds.isEmpty()) {
            log.warn("配置{}没有可分配的楼栋", config.getId());
            return;
        }
        
        // 解析允许的学院
        List<String> allowedColleges = null;
        if (config.getAllowedColleges() != null && !config.getAllowedColleges().isEmpty()) {
            allowedColleges = Arrays.asList(config.getAllowedColleges().split(","));
        }
        
        // 查询所有符合条件的学生（排除管理员）
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUserType, "STUDENT");
        if (allowedColleges != null && !allowedColleges.isEmpty()) {
            userWrapper.in(User::getDepartment, allowedColleges);
        }
        List<User> eligibleStudents = userMapper.selectList(userWrapper);
        
        // 获取所有已选择的学生ID
        List<DormSelectionRecord> existingRecords = recordMapper.selectList(
            new LambdaQueryWrapper<DormSelectionRecord>()
                .eq(DormSelectionRecord::getConfigId, config.getId())
        );
        List<Long> selectedStudentIds = existingRecords.stream()
                .map(DormSelectionRecord::getStudentId)
                .collect(Collectors.toList());
        
        // 为未选择的学生分配宿舍
        int allocatedCount = 0;
        for (User student : eligibleStudents) {
            if (selectedStudentIds.contains(student.getId())) {
                continue; // 已选过
            }
            
            // 检查学生性别是否符合配置
            String genderRestriction = config.getGenderRestriction();
            if (genderRestriction != null && !"ALL".equals(genderRestriction)) {
                if (!genderRestriction.equals(student.getGender())) {
                    continue; // 性别不符
                }
            }
            
            // 执行分配
            boolean success = allocateToStudent(student, config, allowedBuildingIds);
            if (success) {
                allocatedCount++;
                selectedStudentIds.add(student.getId());
            }
        }
        
        log.info("自动分配完成: configId={}, 分配了{}个学生", config.getId(), allocatedCount);
    }
    
    private boolean allocateToStudent(User student, DormSelectionConfig config, List<Long> allowedBuildingIds) {
        try {
            // 查询第一个可用房间
            LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
            roomWrapper.eq(Room::getStatus, "AVAILABLE");
            roomWrapper.apply("occupied_beds < capacity");
            roomWrapper.in(Room::getBuildingId, allowedBuildingIds);
            roomWrapper.last("ORDER BY building_id, room_number LIMIT 1");
            
            Room room = roomMapper.selectOne(roomWrapper);
            if (room == null) {
                log.warn("没有可用房间分配给学生: studentId={}", student.getId());
                return false;
            }
            
            // 获取楼栋信息
            Building building = buildingMapper.selectById(room.getBuildingId());
            if (building == null) {
                return false;
            }
            
            // 计算床位号（A, B, C, D...）
            String bedNumber = String.valueOf((char) ('A' + room.getOccupiedBeds()));
            
            // 创建选宿舍记录
            DormSelectionRecord record = new DormSelectionRecord();
            record.setConfigId(config.getId());
            record.setStudentId(student.getId());
            record.setStudentName(student.getRealName());
            record.setBuildingId(room.getBuildingId());
            record.setBuildingName(building.getBuildingName());
            record.setRoomId(room.getId());
            record.setRoomNumber(room.getRoomNumber());
            record.setBedNumber(bedNumber);
            record.setStatus("AUTO_ALLOCATED");
            record.setSelectTime(LocalDateTime.now());
            
            recordMapper.insert(record);
            
            // 更新房间占用床位数
            room.setOccupiedBeds(room.getOccupiedBeds() + 1);
            if (room.getOccupiedBeds() >= room.getCapacity()) {
                room.setStatus("FULL");
            }
            roomMapper.updateById(room);
            
            log.info("自动分配成功: studentId={}, building={}, room={}, bed={}", 
                    student.getId(), building.getBuildingName(), room.getRoomNumber(), bedNumber);
            return true;
        } catch (Exception e) {
            log.error("自动分配失败: studentId={}", student.getId(), e);
            return false;
        }
    }
    
    private List<Long> parseBuildingIds(String buildingIds) {
        if (buildingIds == null || buildingIds.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.stream(buildingIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
