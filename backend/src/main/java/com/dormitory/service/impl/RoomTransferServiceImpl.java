package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.RoomTransferApproveRequest;
import com.dormitory.dto.request.RoomTransferRequest;
import com.dormitory.dto.response.RoomTransferResponse;
import com.dormitory.entity.*;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.*;
import com.dormitory.service.RoomTransferService;
import com.dormitory.service.StudentAccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTransferServiceImpl implements RoomTransferService {
    
    private final RoomTransferMapper roomTransferMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    private final AccommodationMapper accommodationMapper;
    
    @Override
    @Transactional
    public RoomTransferResponse apply(Long studentId, RoomTransferRequest request) {
        // 1. 检查学生是否存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 2. 检查学生是否有住宿记录
        LambdaQueryWrapper<StudentAccommodation> accWrapper = new LambdaQueryWrapper<>();
        accWrapper.eq(StudentAccommodation::getStudentId, studentId)
                .eq(StudentAccommodation::getStatus, "ACTIVE");
        StudentAccommodation accommodation = accommodationMapper.selectOne(accWrapper);
        if (accommodation == null) {
            throw new BusinessException("您当前没有住宿记录，无法申请换宿舍");
        }
        
        // 3. 检查是否有待处理的换宿舍申请
        LambdaQueryWrapper<RoomTransfer> transferWrapper = new LambdaQueryWrapper<>();
        transferWrapper.eq(RoomTransfer::getStudentId, studentId)
                .eq(RoomTransfer::getStatus, "PENDING");
        if (roomTransferMapper.selectCount(transferWrapper) > 0) {
            throw new BusinessException("您有待处理的换宿舍申请，请等待审批后再提交");
        }
        
        // 4. 检查新房间是否存在且有空位
        Room newRoom = roomMapper.selectById(request.getNewRoomId());
        if (newRoom == null) {
            throw new BusinessException("目标房间不存在");
        }
        if (newRoom.getOccupiedBeds() >= newRoom.getCapacity()) {
            throw new BusinessException("目标房间已满");
        }
        
        // 5. 检查床位是否已被占用
        LambdaQueryWrapper<StudentAccommodation> bedWrapper = new LambdaQueryWrapper<>();
        bedWrapper.eq(StudentAccommodation::getRoomId, request.getNewRoomId())
                .eq(StudentAccommodation::getBedNumber, request.getNewBedNumber())
                .eq(StudentAccommodation::getStatus, "ACTIVE");
        if (accommodationMapper.selectCount(bedWrapper) > 0) {
            throw new BusinessException("目标床位已被占用");
        }
        
        // 6. 获取原宿舍信息
        Room oldRoom = roomMapper.selectById(accommodation.getRoomId());
        Building oldBuilding = oldRoom != null ? buildingMapper.selectById(oldRoom.getBuildingId()) : null;
        
        // 7. 获取新宿舍楼栋信息
        Building newBuilding = buildingMapper.selectById(newRoom.getBuildingId());
        
        // 8. 创建换宿舍申请
        RoomTransfer transfer = new RoomTransfer();
        transfer.setStudentId(studentId);
        transfer.setStudentName(student.getRealName());
        
        // 原宿舍信息
        transfer.setOldRoomId(accommodation.getRoomId());
        transfer.setOldRoomNumber(oldRoom != null ? oldRoom.getRoomNumber() : null);
        transfer.setOldBuildingId(oldRoom != null ? oldRoom.getBuildingId() : null);
        transfer.setOldBuildingName(oldBuilding != null ? oldBuilding.getBuildingName() : null);
        transfer.setOldBedNumber(accommodation.getBedNumber());
        
        // 新宿舍信息
        transfer.setNewRoomId(request.getNewRoomId());
        transfer.setNewRoomNumber(newRoom.getRoomNumber());
        transfer.setNewBuildingId(newRoom.getBuildingId());
        transfer.setNewBuildingName(newBuilding != null ? newBuilding.getBuildingName() : null);
        transfer.setNewBedNumber(request.getNewBedNumber());
        
        transfer.setReason(request.getReason());
        transfer.setStatus("PENDING");
        transfer.setApplyTime(LocalDateTime.now());
        
        roomTransferMapper.insert(transfer);
        
        log.info("学生 {} 申请换宿舍: 从 {} {} 换到 {} {}", 
                student.getRealName(), 
                transfer.getOldBuildingName(), transfer.getOldRoomNumber(),
                transfer.getNewBuildingName(), transfer.getNewRoomNumber());
        
        return convertToResponse(transfer, student);
    }
    
    @Override
    @Transactional
    public void cancel(Long studentId, Long transferId) {
        RoomTransfer transfer = roomTransferMapper.selectById(transferId);
        if (transfer == null) {
            throw new BusinessException("换宿舍申请不存在");
        }
        
        if (!transfer.getStudentId().equals(studentId)) {
            throw new BusinessException("无权取消该申请");
        }
        
        if (!"PENDING".equals(transfer.getStatus())) {
            throw new BusinessException("只能取消待审批的申请");
        }
        
        transfer.setStatus("CANCELLED");
        roomTransferMapper.updateById(transfer);
        
        log.info("学生 {} 取消换宿舍申请: {}", studentId, transferId);
    }
    
    @Override
    public RoomTransferResponse getStudentTransfer(Long studentId) {
        LambdaQueryWrapper<RoomTransfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomTransfer::getStudentId, studentId)
                .eq(RoomTransfer::getStatus, "PENDING")
                .orderByDesc(RoomTransfer::getApplyTime)
                .last("LIMIT 1");
        RoomTransfer transfer = roomTransferMapper.selectOne(wrapper);
        
        if (transfer == null) {
            return null;
        }
        
        User student = userMapper.selectById(studentId);
        return convertToResponse(transfer, student);
    }
    
    @Override
    public List<RoomTransferResponse> getStudentTransferHistory(Long studentId) {
        LambdaQueryWrapper<RoomTransfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomTransfer::getStudentId, studentId)
                .ne(RoomTransfer::getStatus, "CANCELLED")
                .orderByDesc(RoomTransfer::getApplyTime);
        
        User student = userMapper.selectById(studentId);
        return roomTransferMapper.selectList(wrapper).stream()
                .map(t -> convertToResponse(t, student))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public RoomTransferResponse approve(RoomTransferApproveRequest request, Long approverId) {
        RoomTransfer transfer = roomTransferMapper.selectById(request.getId());
        if (transfer == null) {
            throw new BusinessException("换宿舍申请不存在");
        }
        
        if (!"PENDING".equals(transfer.getStatus())) {
            throw new BusinessException("该申请已处理");
        }
        
        User approver = userMapper.selectById(approverId);
        User student = userMapper.selectById(transfer.getStudentId());
        
        if (request.getApproved()) {
            // 审批通过
            // 1. 检查新房间是否还有空位
            Room newRoom = roomMapper.selectById(transfer.getNewRoomId());
            if (newRoom == null) {
                throw new BusinessException("目标房间不存在");
            }
            if (newRoom.getOccupiedBeds() >= newRoom.getCapacity()) {
                throw new BusinessException("目标房间已满，无法审批通过");
            }
            
            // 2. 检查床位是否已被占用
            LambdaQueryWrapper<StudentAccommodation> bedWrapper = new LambdaQueryWrapper<>();
            bedWrapper.eq(StudentAccommodation::getRoomId, transfer.getNewRoomId())
                    .eq(StudentAccommodation::getBedNumber, transfer.getNewBedNumber())
                    .eq(StudentAccommodation::getStatus, "ACTIVE");
            if (accommodationMapper.selectCount(bedWrapper) > 0) {
                throw new BusinessException("目标床位已被占用，无法审批通过");
            }
            
            // 3. 更新原住宿记录
            LambdaQueryWrapper<StudentAccommodation> accWrapper = new LambdaQueryWrapper<>();
            accWrapper.eq(StudentAccommodation::getStudentId, transfer.getStudentId())
                    .eq(StudentAccommodation::getStatus, "ACTIVE");
            StudentAccommodation accommodation = accommodationMapper.selectOne(accWrapper);
            
            if (accommodation != null) {
                // 更新原房间床位数
                Room oldRoom = roomMapper.selectById(accommodation.getRoomId());
                if (oldRoom != null) {
                    oldRoom.setOccupiedBeds(Math.max(0, oldRoom.getOccupiedBeds() - 1));
                    if (oldRoom.getOccupiedBeds() == 0) {
                        oldRoom.setStatus("AVAILABLE");
                    } else {
                        oldRoom.setStatus("PARTIAL");
                    }
                    roomMapper.updateById(oldRoom);
                    
                    // 更新楼栋可用床位数
                    Building oldBuilding = buildingMapper.selectById(oldRoom.getBuildingId());
                    if (oldBuilding != null) {
                        oldBuilding.setAvailableBeds(oldBuilding.getAvailableBeds() + 1);
                        buildingMapper.updateById(oldBuilding);
                    }
                }
                
                // 设置原住宿记录为已转移
                accommodation.setStatus("TRANSFERRED");
                accommodation.setActualCheckOutDate(java.time.LocalDate.now());
                accommodationMapper.updateById(accommodation);
                
                // 创建新住宿记录
                StudentAccommodation newAccommodation = new StudentAccommodation();
                newAccommodation.setStudentId(transfer.getStudentId());
                newAccommodation.setRoomId(transfer.getNewRoomId());
                newAccommodation.setBuildingId(transfer.getNewBuildingId());
                newAccommodation.setBedNumber(transfer.getNewBedNumber());
                newAccommodation.setCheckInDate(java.time.LocalDate.now());
                newAccommodation.setDeposit(accommodation.getDeposit());
                newAccommodation.setStatus("ACTIVE");
                newAccommodation.setRemark("换宿舍转入");
                accommodationMapper.insert(newAccommodation);
                
                // 更新新房间床位数
                newRoom.setOccupiedBeds(newRoom.getOccupiedBeds() + 1);
                if (newRoom.getOccupiedBeds() >= newRoom.getCapacity()) {
                    newRoom.setStatus("FULL");
                } else {
                    newRoom.setStatus("PARTIAL");
                }
                roomMapper.updateById(newRoom);
                
                // 更新新楼栋可用床位数
                Building newBuilding = buildingMapper.selectById(newRoom.getBuildingId());
                if (newBuilding != null) {
                    newBuilding.setAvailableBeds(Math.max(0, newBuilding.getAvailableBeds() - 1));
                    buildingMapper.updateById(newBuilding);
                }
            }
            
            // 更新申请状态
            transfer.setStatus("APPROVED");
            transfer.setApproveTime(LocalDateTime.now());
            transfer.setApproverId(approverId);
            transfer.setApproverName(approver != null ? approver.getRealName() : null);
            
            log.info("换宿舍申请 {} 已审批通过，学生 {} 从 {} 换到 {}", 
                    transfer.getId(), transfer.getStudentName(),
                    transfer.getOldRoomNumber(), transfer.getNewRoomNumber());
        } else {
            // 审批拒绝
            transfer.setStatus("REJECTED");
            transfer.setApproveTime(LocalDateTime.now());
            transfer.setApproverId(approverId);
            transfer.setApproverName(approver != null ? approver.getRealName() : null);
            transfer.setRejectReason(request.getRejectReason());
            
            log.info("换宿舍申请 {} 已拒绝，学生 {}，原因: {}", 
                    transfer.getId(), transfer.getStudentName(), request.getRejectReason());
        }
        
        roomTransferMapper.updateById(transfer);
        return convertToResponse(transfer, student);
    }
    
    @Override
    public PageResult<RoomTransferResponse> getPage(Integer page, Integer size, String status, Long buildingId, String keyword) {
        Page<RoomTransfer> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<RoomTransfer> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(RoomTransfer::getStatus, status);
        }
        if (buildingId != null) {
            wrapper.and(w -> w.eq(RoomTransfer::getOldBuildingId, buildingId)
                    .or().eq(RoomTransfer::getNewBuildingId, buildingId));
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(RoomTransfer::getStudentName, keyword)
                    .or().like(RoomTransfer::getOldRoomNumber, keyword)
                    .or().like(RoomTransfer::getNewRoomNumber, keyword));
        }
        
        wrapper.orderByDesc(RoomTransfer::getApplyTime);
        
        IPage<RoomTransfer> pageResult = roomTransferMapper.selectPage(mpPage, wrapper);
        
        List<RoomTransferResponse> responses = pageResult.getRecords().stream()
                .map(t -> {
                    User student = userMapper.selectById(t.getStudentId());
                    return convertToResponse(t, student);
                })
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    @Override
    public RoomTransferResponse getById(Long id) {
        RoomTransfer transfer = roomTransferMapper.selectById(id);
        if (transfer == null) {
            throw new BusinessException("换宿舍申请不存在");
        }
        
        User student = userMapper.selectById(transfer.getStudentId());
        return convertToResponse(transfer, student);
    }
    
    private RoomTransferResponse convertToResponse(RoomTransfer transfer, User student) {
        return RoomTransferResponse.builder()
                .id(transfer.getId())
                .studentId(transfer.getStudentId())
                .studentName(transfer.getStudentName())
                .studentIdNumber(student != null ? student.getStudentId() : null)
                .department(student != null ? student.getDepartment() : null)
                .className(student != null ? student.getClassName() : null)
                .oldRoomId(transfer.getOldRoomId())
                .oldRoomNumber(transfer.getOldRoomNumber())
                .oldBuildingId(transfer.getOldBuildingId())
                .oldBuildingName(transfer.getOldBuildingName())
                .oldBedNumber(transfer.getOldBedNumber())
                .newRoomId(transfer.getNewRoomId())
                .newRoomNumber(transfer.getNewRoomNumber())
                .newBuildingId(transfer.getNewBuildingId())
                .newBuildingName(transfer.getNewBuildingName())
                .newBedNumber(transfer.getNewBedNumber())
                .reason(transfer.getReason())
                .status(transfer.getStatus())
                .applyTime(transfer.getApplyTime())
                .approveTime(transfer.getApproveTime())
                .approverId(transfer.getApproverId())
                .approverName(transfer.getApproverName())
                .rejectReason(transfer.getRejectReason())
                .remark(transfer.getRemark())
                .createTime(transfer.getCreateTime())
                .build();
    }
}
