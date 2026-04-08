package com.dormitory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.RoomTransferApproveRequest;
import com.dormitory.dto.request.RoomTransferRequest;
import com.dormitory.dto.response.RoomTransferResponse;
import com.dormitory.entity.User;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.RoomTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 换宿舍申请控制器
 */
@Tag(name = "换宿舍管理", description = "换宿舍申请相关接口")
@RestController
@RequestMapping("/room-transfer")
@RequiredArgsConstructor
public class RoomTransferController {
    
    private final RoomTransferService roomTransferService;
    private final UserMapper userMapper;
    
    // ==================== 学生端接口 ====================
    
    @Operation(summary = "学生申请换宿舍")
    @PostMapping("/apply")
    public Result<RoomTransferResponse> apply(@RequestBody RoomTransferRequest request, Authentication authentication) {
        Long studentId = getUserIdFromAuth(authentication);
        RoomTransferResponse response = roomTransferService.apply(studentId, request);
        return Result.success("申请成功", response);
    }
    
    @Operation(summary = "取消换宿舍申请")
    @DeleteMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id, Authentication authentication) {
        Long studentId = getUserIdFromAuth(authentication);
        roomTransferService.cancel(studentId, id);
        return Result.success("取消成功");
    }
    
    @Operation(summary = "获取当前学生的换宿舍申请")
    @GetMapping({"/my", "/my-transfer"})
    public Result<RoomTransferResponse> getMyTransfer(Authentication authentication) {
        Long studentId = getUserIdFromAuth(authentication);

        RoomTransferResponse response = roomTransferService.getStudentTransfer(studentId);
        return Result.success(response);
    }
    
    @Operation(summary = "获取当前学生的换宿舍申请历史")
    @GetMapping({"/my/history", "/my-transfer/history"})
    public Result<List<RoomTransferResponse>> getMyHistory(Authentication authentication) {
        Long studentId = getUserIdFromAuth(authentication);
        List<RoomTransferResponse> history = roomTransferService.getStudentTransferHistory(studentId);
        return Result.success(history);
    }
    
    // ==================== 管理端接口 ====================
    
    @Operation(summary = "审批换宿舍申请")
    @PostMapping("/approve")
    public Result<RoomTransferResponse> approve(@RequestBody RoomTransferApproveRequest request, Authentication authentication) {
        Long approverId = getUserIdFromAuth(authentication);
        RoomTransferResponse response = roomTransferService.approve(request, approverId);
        return Result.success("审批成功", response);
    }
    
    @Operation(summary = "分页获取换宿舍申请列表")
    @GetMapping("/page")
    public Result<PageResult<RoomTransferResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String keyword) {
        PageResult<RoomTransferResponse> result = roomTransferService.getPage(page, size, status, buildingId, keyword);
        return Result.success(result);
    }
    
    @Operation(summary = "获取换宿舍申请详情")
    @GetMapping("/{id}")
    public Result<RoomTransferResponse> getById(@PathVariable Long id) {
        RoomTransferResponse response = roomTransferService.getById(id);
        return Result.success(response);
    }
    
    // 辅助方法：从认证信息中获取用户ID
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        String username = authentication.getName();
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        return user != null ? user.getId() : null;
    }
}
