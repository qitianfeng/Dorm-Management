package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.CheckInRequest;
import com.dormitory.dto.request.CheckOutRequest;
import com.dormitory.dto.request.TransferRequest;
import com.dormitory.dto.response.AccommodationResponse;
import com.dormitory.service.StudentAccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生住宿管理控制器
 */
@Tag(name = "学生住宿管理", description = "入住、调宿、退宿等接口")
@RestController
@RequestMapping("/accommodation")
@RequiredArgsConstructor
public class StudentAccommodationController {
    
    private final StudentAccommodationService accommodationService;
    
    @Operation(summary = "学生入住")
    @PostMapping("/check-in")
    public Result<AccommodationResponse> checkIn(@Valid @RequestBody CheckInRequest request) {
        AccommodationResponse response = accommodationService.checkIn(request);
        return Result.success("入住成功", response);
    }
    
    @Operation(summary = "学生调宿")
    @PostMapping("/transfer")
    public Result<AccommodationResponse> transfer(@Valid @RequestBody TransferRequest request) {
        AccommodationResponse response = accommodationService.transfer(request);
        return Result.success("调宿成功", response);
    }
    
    @Operation(summary = "学生退宿")
    @PostMapping("/check-out")
    public Result<AccommodationResponse> checkOut(@Valid @RequestBody CheckOutRequest request) {
        AccommodationResponse response = accommodationService.checkOut(request);
        return Result.success("退宿成功", response);
    }
    
    @Operation(summary = "获取住宿记录详情")
    @GetMapping("/{id}")
    public Result<AccommodationResponse> getById(@PathVariable Long id) {
        AccommodationResponse response = accommodationService.getById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "根据学生ID获取当前住宿信息")
    @GetMapping("/student/{studentId}")
    public Result<AccommodationResponse> getCurrentByStudentId(@PathVariable Long studentId) {
        AccommodationResponse response = accommodationService.getCurrentByStudentId(studentId);
        return Result.success(response);
    }
    
    @Operation(summary = "根据房间ID获取入住学生列表")
    @GetMapping("/room/{roomId}")
    public Result<List<AccommodationResponse>> getByRoomId(@PathVariable Long roomId) {
        List<AccommodationResponse> responses = accommodationService.getByRoomId(roomId);
        return Result.success(responses);
    }
    
    @Operation(summary = "根据楼栋ID获取所有入住记录")
    @GetMapping("/building/{buildingId}")
    public Result<List<AccommodationResponse>> getByBuildingId(@PathVariable Long buildingId) {
        List<AccommodationResponse> responses = accommodationService.getByBuildingId(buildingId);
        return Result.success(responses);
    }
    
    @Operation(summary = "分页查询住宿记录")
    @GetMapping("/page")
    public Result<PageResult<AccommodationResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String keyword) {
        PageResult<AccommodationResponse> result = accommodationService.getPage(page, size, buildingId, roomId, keyword);
        return Result.success(result);
    }
    
    @Operation(summary = "获取空床位列表")
    @GetMapping("/available-beds")
    public Result<List<AccommodationResponse>> getAvailableBeds(
            @RequestParam(required = false) Long buildingId) {
        List<AccommodationResponse> responses = accommodationService.getAvailableBeds(buildingId);
        return Result.success(responses);
    }
}
