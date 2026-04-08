package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.FeePaymentRequest;
import com.dormitory.dto.request.FeeRequest;
import com.dormitory.dto.response.FeeResponse;
import com.dormitory.entity.Fee;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.FeeMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.FeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {
    
    private final FeeMapper feeMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public FeeResponse create(FeeRequest request) {
        User student = userMapper.selectById(request.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        Fee fee = new Fee();
        fee.setStudentId(request.getStudentId());
        fee.setFeeType(request.getFeeType());
        fee.setAmount(request.getAmount());
        fee.setDueTime(request.getDueTime());
        fee.setRemark(request.getRemark());
        fee.setOperatorId(request.getOperatorId());
        fee.setStatus("UNPAID");
        
        feeMapper.insert(fee);
        return buildResponse(fee);
    }
    
    @Override
    @Transactional
    public FeeResponse update(Long id, FeeRequest request) {
        Fee fee = feeMapper.selectById(id);
        if (fee == null) {
            throw new BusinessException("缴费记录不存在");
        }
        
        fee.setFeeType(request.getFeeType());
        fee.setAmount(request.getAmount());
        fee.setDueTime(request.getDueTime());
        fee.setRemark(request.getRemark());
        
        feeMapper.updateById(fee);
        return buildResponse(fee);
    }
    
    @Override
    @Transactional
    public FeeResponse pay(Long id, FeePaymentRequest request) {
        Fee fee = feeMapper.selectById(id);
        if (fee == null) {
            throw new BusinessException("缴费记录不存在");
        }
        
        if ("PAID".equals(fee.getStatus())) {
            throw new BusinessException("该费用已缴纳");
        }
        
        fee.setStatus(request.getStatus() != null ? request.getStatus() : "PAID");
        fee.setPaymentMethod(request.getPaymentMethod());
        fee.setTransactionId(request.getTransactionId());
        fee.setPaidTime(LocalDateTime.now());
        
        feeMapper.updateById(fee);
        return buildResponse(fee);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Fee fee = feeMapper.selectById(id);
        if (fee == null) {
            throw new BusinessException("缴费记录不存在");
        }
        feeMapper.deleteById(id);
    }
    
    @Override
    public FeeResponse getById(Long id) {
        Fee fee = feeMapper.selectById(id);
        if (fee == null) {
            throw new BusinessException("缴费记录不存在");
        }
        return buildResponse(fee);
    }
    
    @Override
    public List<FeeResponse> getByStudentId(Long studentId) {
        LambdaQueryWrapper<Fee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Fee::getStudentId, studentId);
        return feeMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FeeResponse> getUnpaid() {
        LambdaQueryWrapper<Fee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Fee::getStatus, "UNPAID");
        return feeMapper.selectList(wrapper).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<FeeResponse> getPage(Integer page, Integer size, Long studentId, String status, String feeType) {
        Page<Fee> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Fee> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(Fee::getStudentId, studentId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Fee::getStatus, status);
        }
        if (StringUtils.hasText(feeType)) {
            wrapper.eq(Fee::getFeeType, feeType);
        }
        wrapper.orderByDesc(Fee::getCreateTime);
        
        IPage<Fee> pageResult = feeMapper.selectPage(mpPage, wrapper);
        
        List<FeeResponse> responses = pageResult.getRecords().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    private FeeResponse buildResponse(Fee fee) {
        User student = userMapper.selectById(fee.getStudentId());
        User operator = fee.getOperatorId() != null ? userMapper.selectById(fee.getOperatorId()) : null;
        
        return FeeResponse.builder()
                .id(fee.getId())
                .studentId(fee.getStudentId())
                .studentName(student != null ? student.getRealName() : null)
                .studentIdNumber(student != null ? student.getStudentId() : null)
                .feeType(fee.getFeeType())
                .amount(fee.getAmount())
                .dueTime(fee.getDueTime())
                .paidTime(fee.getPaidTime())
                .status(fee.getStatus())
                .paymentMethod(fee.getPaymentMethod())
                .transactionId(fee.getTransactionId())
                .remark(fee.getRemark())
                .operatorId(fee.getOperatorId())
                .operatorName(operator != null ? operator.getRealName() : null)
                .createTime(fee.getCreateTime())
                .updateTime(fee.getUpdateTime())
                .build();
    }
}
