package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.dto.request.DisciplinaryRequest;
import com.dormitory.dto.response.DisciplinaryResponse;
import com.dormitory.entity.DisciplinaryRecord;
import com.dormitory.entity.Room;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.DisciplinaryRecordMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.DisciplinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisciplinaryServiceImpl implements DisciplinaryService {
    
    private final DisciplinaryRecordMapper disciplinaryMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public DisciplinaryResponse create(DisciplinaryRequest request) {
        // 根据学号查找学生
        User student = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getStudentId, request.getStudentId())
        );
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        DisciplinaryRecord record = new DisciplinaryRecord();
        record.setStudentId(student.getId());
        record.setStudentName(student.getRealName());
        record.setViolationType(request.getViolationType());
        record.setDescription(request.getDescription());
        record.setViolationTime(LocalDateTime.parse(request.getViolationTime(), DATE_FORMATTER));
        record.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        record.setRemark(request.getProcessNote());
        record.setRecorderId(1L); // 管理员
        
        disciplinaryMapper.insert(record);
        return buildResponse(record);
    }
    
    @Override
    @Transactional
    public DisciplinaryResponse update(Long id, DisciplinaryRequest request) {
        DisciplinaryRecord record = disciplinaryMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("违纪记录不存在");
        }
        
        if (StringUtils.hasText(request.getStatus())) {
            record.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getViolationType())) {
            record.setViolationType(request.getViolationType());
        }
        if (StringUtils.hasText(request.getDescription())) {
            record.setDescription(request.getDescription());
        }
        if (request.getViolationTime() != null) {
            record.setViolationTime(LocalDateTime.parse(request.getViolationTime(), DATE_FORMATTER));
        }
        if (request.getProcessNote() != null) {
            record.setRemark(request.getProcessNote());
        }
        
        disciplinaryMapper.updateById(record);
        return buildResponse(record);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        DisciplinaryRecord record = disciplinaryMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("违纪记录不存在");
        }
        disciplinaryMapper.deleteById(id);
    }
    
    @Override
    public DisciplinaryResponse getById(Long id) {
        DisciplinaryRecord record = disciplinaryMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("违纪记录不存在");
        }
        return buildResponse(record);
    }
    
    @Override
    public PageResult<DisciplinaryResponse> getPage(Integer page, Integer size, String keyword, String type, String status) {
        Page<DisciplinaryRecord> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<DisciplinaryRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(DisciplinaryRecord::getStudentName, keyword)
                .or()
                .like(DisciplinaryRecord::getStudentId, keyword)
            );
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(DisciplinaryRecord::getViolationType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(DisciplinaryRecord::getStatus, status);
        }
        
        wrapper.orderByDesc(DisciplinaryRecord::getCreateTime);
        
        IPage<DisciplinaryRecord> pageResult = disciplinaryMapper.selectPage(mpPage, wrapper);
        
        List<DisciplinaryResponse> responses = pageResult.getRecords().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
        
        return PageResult.of(responses, pageResult.getTotal(), (long) size, (long) page);
    }
    
    private DisciplinaryResponse buildResponse(DisciplinaryRecord record) {
        User student = record.getStudentId() != null ? userMapper.selectById(record.getStudentId()) : null;
        Room room = record.getRoomId() != null ? roomMapper.selectById(record.getRoomId()) : null;
        String buildingName = null;
        String roomNumber = null;
        if (room != null) {
            roomNumber = room.getRoomNumber();
            if (room.getBuildingId() != null) {
                buildingName = "楼栋" + room.getBuildingId();
            }
        }
        
        return DisciplinaryResponse.builder()
                .id(record.getId())
                .studentId(record.getStudentId())
                .studentName(record.getStudentName())
                .studentIdNumber(student != null ? student.getStudentId() : null)
                .roomId(record.getRoomId())
                .buildingName(buildingName)
                .roomNumber(roomNumber)
                .violationType(record.getViolationType())
                .description(record.getDescription())
                .violationTime(record.getViolationTime())
                .punishment(record.getPunishment())
                .fine(record.getFine())
                .status(record.getStatus())
                .processNote(record.getRemark())
                .recorderId(record.getRecorderId())
                .recorderName(record.getRecorderName())
                .remark(record.getRemark())
                .createTime(record.getCreateTime())
                .updateTime(record.getUpdateTime())
                .build();
    }
}
