package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.dto.request.H5RepairRequest;
import com.dormitory.dto.request.DormSelectionRequest;
import com.dormitory.dto.response.AccommodationResponse;
import com.dormitory.dto.response.AnnouncementResponse;
import com.dormitory.dto.response.RepairResponse;
import com.dormitory.entity.DormSelectionConfig;
import com.dormitory.entity.DormSelectionRecord;
import com.dormitory.entity.Room;
import com.dormitory.entity.User;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.AnnouncementService;
import com.dormitory.service.DormSelectionService;
import com.dormitory.service.RepairService;
import com.dormitory.service.StudentAccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5移动端控制器
 */
@Tag(name = "H5移动端", description = "学生端移动端专用接口")
@RestController
@RequestMapping("/h5")
@RequiredArgsConstructor
public class H5Controller {
    
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final AnnouncementService announcementService;
    private final RepairService repairService;
    private final StudentAccommodationService accommodationService;
    private final DormSelectionService dormSelectionService;
    
    @Operation(summary = "获取当前学生信息（含住宿信息）")
    @GetMapping("/student/info")
    public Result<Map<String, Object>> getStudentInfo(Authentication authentication) {
        String username = authentication.getName();
        
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("studentId", user.getStudentId());
        result.put("department", user.getDepartment());
        result.put("className", user.getClassName());
        result.put("phone", user.getPhone());
        result.put("userType", user.getUserType());
        result.put("avatar", user.getAvatar());
        
        // 获取住宿信息
        AccommodationResponse accommodation = null;
        try {
            accommodation = accommodationService.getCurrentByStudentId(user.getId());
        } catch (Exception e) {
            // 学生未分配宿舍
        }
        result.put("accommodation", accommodation);
        
        return Result.success(result);
    }
    
    @Operation(summary = "获取已发布公告列表")
    @GetMapping("/announcements")
    public Result<List<AnnouncementResponse>> getAnnouncements() {
        List<AnnouncementResponse> announcements = announcementService.getPublished();
        return Result.success(announcements);
    }
    
    @Operation(summary = "提交报修申请")
    @PostMapping("/repair")
    public Result<RepairResponse> submitRepair(@RequestBody H5RepairRequest request, Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 根据房间号查找房间（取第一个匹配的）
        List<Room> rooms = roomMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Room>()
                .eq(Room::getRoomNumber, request.getRoomNumber())
                .last("LIMIT 1")
        );
        
        if (rooms.isEmpty()) {
            return Result.error("房间号不存在");
        }
        
        Room room = rooms.get(0);
        
        // 构建 RepairRequestDto
        com.dormitory.dto.request.RepairRequestDto dto = new com.dormitory.dto.request.RepairRequestDto();
        dto.setStudentId(user.getId());
        dto.setRoomId(room.getId());
        dto.setRepairType(request.getRepairType());
        dto.setDescription(request.getDescription());
        dto.setImages(request.getImages());
        dto.setPriority(request.getPriority());
        
        RepairResponse response = repairService.create(dto);
        return Result.success("提交成功", response);
    }
    
    @Operation(summary = "获取我的报修记录")
    @GetMapping("/repairs")
    public Result<List<RepairResponse>> getMyRepairs(Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        List<RepairResponse> repairs = repairService.getByStudentId(user.getId());
        return Result.success(repairs);
    }
    
    // ==================== 选宿舍功能 ====================
    
    @Operation(summary = "获取当前选宿舍配置")
    @GetMapping("/dorm-selection/config")
    public Result<DormSelectionConfig> getDormSelectionConfig() {
        DormSelectionConfig config = dormSelectionService.getActiveConfig();
        return Result.success(config);
    }
    
    @Operation(summary = "获取可选房间列表")
    @GetMapping("/dorm-selection/rooms")
    public Result<List<Object>> getAvailableRooms(
            @RequestParam(required = false) Long buildingId,
            Authentication authentication) {
        DormSelectionConfig config = dormSelectionService.getActiveConfig();
        if (config == null) {
            return Result.error("当前没有可用的选宿舍配置");
        }
        List<Object> rooms = dormSelectionService.getAvailableRooms(config.getId(), buildingId);
        return Result.success(rooms);
    }
    
    @Operation(summary = "学生选择宿舍")
    @PostMapping("/dorm-selection/select")
    public Result<DormSelectionRecord> selectRoom(
            @RequestBody DormSelectionRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        DormSelectionRecord record = dormSelectionService.selectRoom(user.getId(), request);
        return Result.success("选宿舍成功", record);
    }
    
    @Operation(summary = "获取我的选宿舍记录")
    @GetMapping("/dorm-selection/my")
    public Result<DormSelectionRecord> getMySelection(Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        DormSelectionRecord record = dormSelectionService.getStudentSelection(user.getId());
        return Result.success(record);
    }
    
    @Operation(summary = "取消选宿舍")
    @DeleteMapping("/dorm-selection/cancel")
    public Result<?> cancelSelection(Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        dormSelectionService.cancelSelection(user.getId());
        return Result.success("取消成功");
    }
    
    @Operation(summary = "自动分配宿舍")
    @PostMapping("/dorm-selection/auto-allocate")
    public Result<DormSelectionRecord> autoAllocate(Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        DormSelectionRecord record = dormSelectionService.autoAllocate(user.getId());
        return Result.success("自动分配成功", record);
    }
    
    // ==================== 退宿功能 ====================
    
    @Operation(summary = "申请退宿")
    @PostMapping("/check-out")
    public Result<?> applyCheckOut(Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        AccommodationResponse accommodation = accommodationService.getCurrentByStudentId(user.getId());
        if (accommodation == null) {
            return Result.error("您当前没有住宿记录");
        }
        
        // 执行退宿
        com.dormitory.dto.request.CheckOutRequest request = new com.dormitory.dto.request.CheckOutRequest();
        request.setAccommodationId(accommodation.getId());
        request.setCheckOutDate(java.time.LocalDate.now());
        request.setReason("学生主动申请退宿");
        
        accommodationService.checkOut(request);
        return Result.success("退宿申请成功");
    }
}
