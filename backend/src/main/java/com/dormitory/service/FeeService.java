package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.FeePaymentRequest;
import com.dormitory.dto.request.FeeRequest;
import com.dormitory.dto.response.FeeResponse;

import java.util.List;

/**
 * 缴费管理服务接口
 */
public interface FeeService {
    
    /**
     * 创建缴费记录
     */
    FeeResponse create(FeeRequest request);
    
    /**
     * 更新缴费记录
     */
    FeeResponse update(Long id, FeeRequest request);
    
    /**
     * 缴费支付
     */
    FeeResponse pay(Long id, FeePaymentRequest request);
    
    /**
     * 删除缴费记录
     */
    void delete(Long id);
    
    /**
     * 获取详情
     */
    FeeResponse getById(Long id);
    
    /**
     * 根据学生ID获取缴费记录
     */
    List<FeeResponse> getByStudentId(Long studentId);
    
    /**
     * 获取未缴费记录
     */
    List<FeeResponse> getUnpaid();
    
    /**
     * 分页查询
     */
    PageResult<FeeResponse> getPage(Integer page, Integer size, Long studentId, String status, String feeType);
}
