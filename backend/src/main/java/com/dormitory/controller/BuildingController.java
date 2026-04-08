package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.BuildingRequest;
import com.dormitory.dto.request.RoomRequest;
import com.dormitory.dto.response.BuildingResponse;
import com.dormitory.dto.response.RoomResponse;
import com.dormitory.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宿舍楼管理控制器
 */
@Tag(name = "宿舍楼管理", description = "宿舍楼和房间管理接口")
@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {
    
    private final BuildingService buildingService;
    
    // ========== 宿舍楼管理 ==========
    
    @Operation(summary = "创建宿舍楼")
    @PostMapping
    public Result<BuildingResponse> createBuilding(@Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.createBuilding(request);
        return Result.success("创建成功", response);
    }
    
    @Operation(summary = "更新宿舍楼")
    @PutMapping("/{id}")
    public Result<BuildingResponse> updateBuilding(@PathVariable Long id, @Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.updateBuilding(id, request);
        return Result.success("更新成功", response);
    }
    
    @Operation(summary = "删除宿舍楼")
    @DeleteMapping("/{id}")
    public Result<?> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取宿舍楼详情")
    @GetMapping("/{id}")
    public Result<BuildingResponse> getBuildingById(@PathVariable Long id) {
        BuildingResponse response = buildingService.getBuildingById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "获取所有宿舍楼列表")
    @GetMapping("/list")
    public Result<List<BuildingResponse>> getAllBuildings() {
        List<BuildingResponse> responses = buildingService.getAllBuildings();
        return Result.success(responses);
    }
    
    @Operation(summary = "分页查询宿舍楼")
    @GetMapping("/page")
    public Result<PageResult<BuildingResponse>> getBuildingsPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        PageResult<BuildingResponse> result = buildingService.getBuildingsPage(page, size, keyword);
        return Result.success(result);
    }
    
    // ========== 房间管理 ==========
    
    @Operation(summary = "创建房间")
    @PostMapping("/room")
    public Result<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse response = buildingService.createRoom(request);
        return Result.success("创建成功", response);
    }
    
    @Operation(summary = "更新房间")
    @PutMapping("/room/{id}")
    public Result<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        RoomResponse response = buildingService.updateRoom(id, request);
        return Result.success("更新成功", response);
    }
    
    @Operation(summary = "删除房间")
    @DeleteMapping("/room/{id}")
    public Result<?> deleteRoom(@PathVariable Long id) {
        buildingService.deleteRoom(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取房间详情")
    @GetMapping("/room/{id}")
    public Result<RoomResponse> getRoomById(@PathVariable Long id) {
        RoomResponse response = buildingService.getRoomById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "根据楼栋ID获取房间列表")
    @GetMapping("/{buildingId}/rooms")
    public Result<List<RoomResponse>> getRoomsByBuildingId(@PathVariable Long buildingId) {
        List<RoomResponse> responses = buildingService.getRoomsByBuildingId(buildingId);
        return Result.success(responses);
    }
    
    @Operation(summary = "分页查询房间")
    @GetMapping("/room/page")
    public Result<PageResult<RoomResponse>> getRoomsPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String keyword) {
        PageResult<RoomResponse> result = buildingService.getRoomsPage(page, size, buildingId, keyword);
        return Result.success(result);
    }
}
