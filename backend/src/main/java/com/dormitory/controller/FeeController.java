package com.dormitory.controller;

import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.dto.request.FeePaymentRequest;
import com.dormitory.dto.request.FeeRequest;
import com.dormitory.dto.response.FeeResponse;
import com.dormitory.service.FeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 缴费管理控制器
 */
@Tag(name = "缴费管理", description = "缴费记录和支付接口")
@RestController
@RequestMapping("/fee")
@RequiredArgsConstructor
public class FeeController {
    
    private final FeeService feeService;
    
    @Operation(summary = "创建缴费记录")
    @PostMapping
    public Result<FeeResponse> create(@Valid @RequestBody FeeRequest request) {
        FeeResponse response = feeService.create(request);
        return Result.success("创建成功", response);
    }
    
    @Operation(summary = "更新缴费记录")
    @PutMapping("/{id}")
    public Result<FeeResponse> update(@PathVariable Long id, @Valid @RequestBody FeeRequest request) {
        FeeResponse response = feeService.update(id, request);
        return Result.success("更新成功", response);
    }
    
    @Operation(summary = "缴费支付")
    @PutMapping("/{id}/pay")
    public Result<FeeResponse> pay(@PathVariable Long id, @RequestBody FeePaymentRequest request) {
        FeeResponse response = feeService.pay(id, request);
        return Result.success("缴费成功", response);
    }
    
    @Operation(summary = "删除缴费记录")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        feeService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取缴费详情")
    @GetMapping("/{id}")
    public Result<FeeResponse> getById(@PathVariable Long id) {
        FeeResponse response = feeService.getById(id);
        return Result.success(response);
    }
    
    @Operation(summary = "根据学生ID获取缴费记录")
    @GetMapping("/student/{studentId}")
    public Result<List<FeeResponse>> getByStudentId(@PathVariable Long studentId) {
        List<FeeResponse> responses = feeService.getByStudentId(studentId);
        return Result.success(responses);
    }
    
    @Operation(summary = "获取未缴费记录")
    @GetMapping("/unpaid")
    public Result<List<FeeResponse>> getUnpaid() {
        List<FeeResponse> responses = feeService.getUnpaid();
        return Result.success(responses);
    }
    
    @Operation(summary = "分页查询缴费记录")
    @GetMapping("/page")
    public Result<PageResult<FeeResponse>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String feeType) {
        PageResult<FeeResponse> result = feeService.getPage(page, size, studentId, status, feeType);
        return Result.success(result);
    }
}
