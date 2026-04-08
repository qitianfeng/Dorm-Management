package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.CheckInRequest;
import com.dormitory.dto.request.CheckOutRequest;
import com.dormitory.dto.request.TransferRequest;
import com.dormitory.dto.response.AccommodationResponse;

import java.util.List;

/**
 * 学生住宿服务接口
 */
public interface StudentAccommodationService {
    
    /**
     * 学生入住
     */
    AccommodationResponse checkIn(CheckInRequest request);
    
    /**
     * 学生调宿
     */
    AccommodationResponse transfer(TransferRequest request);
    
    /**
     * 学生退宿
     */
    AccommodationResponse checkOut(CheckOutRequest request);
    
    /**
     * 获取住宿记录详情
     */
    AccommodationResponse getById(Long id);
    
    /**
     * 根据学生ID获取当前住宿信息
     */
    AccommodationResponse getCurrentByStudentId(Long studentId);
    
    /**
     * 根据房间ID获取入住学生列表
     */
    List<AccommodationResponse> getByRoomId(Long roomId);
    
    /**
     * 根据楼栋ID获取所有入住记录
     */
    List<AccommodationResponse> getByBuildingId(Long buildingId);
    
    /**
     * 分页查询住宿记录
     */
    PageResult<AccommodationResponse> getPage(Integer page, Integer size, Long buildingId, Long roomId, String keyword);
    
    /**
     * 获取空床位列表
     */
    List<AccommodationResponse> getAvailableBeds(Long buildingId);
}
