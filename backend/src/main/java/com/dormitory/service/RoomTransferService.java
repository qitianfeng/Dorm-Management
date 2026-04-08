package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.RoomTransferApproveRequest;
import com.dormitory.dto.request.RoomTransferRequest;
import com.dormitory.dto.response.RoomTransferResponse;

import java.util.List;

/**
 * 换宿舍申请服务接口
 */
public interface RoomTransferService {
    
    /**
     * 学生申请换宿舍
     */
    RoomTransferResponse apply(Long studentId, RoomTransferRequest request);
    
    /**
     * 取消换宿舍申请
     */
    void cancel(Long studentId, Long transferId);
    
    /**
     * 获取学生的当前换宿舍申请（待审批的）
     */
    RoomTransferResponse getStudentTransfer(Long studentId);
    
    /**
     * 获取学生的换宿舍申请历史
     */
    List<RoomTransferResponse> getStudentTransferHistory(Long studentId);
    
    /**
     * 审批换宿舍申请（管理端）
     */
    RoomTransferResponse approve(RoomTransferApproveRequest request, Long approverId);
    
    /**
     * 分页获取换宿舍申请列表（管理端）
     */
    PageResult<RoomTransferResponse> getPage(Integer page, Integer size, String status, Long buildingId, String keyword);
    
    /**
     * 获取换宿舍申请详情
     */
    RoomTransferResponse getById(Long id);
}
