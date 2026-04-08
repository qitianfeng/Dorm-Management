package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.dto.response.StatisticsResponse;
import com.dormitory.entity.*;
import com.dormitory.mapper.*;
import com.dormitory.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final AccommodationMapper accommodationMapper;
    private final RepairRequestMapper repairMapper;
    private final HygieneInspectionMapper hygieneMapper;
    private final FeeMapper feeMapper;
    private final VisitorMapper visitorMapper;
    private final UserMapper userMapper;
    
    @Override
    public StatisticsResponse getOverview() {
        Long totalBuildings = buildingMapper.selectCount(null);
        Long totalRooms = roomMapper.selectCount(null);
        
        List<Building> buildings = buildingMapper.selectList(null);
        Long totalBeds = buildings.stream().mapToLong(b -> b.getTotalBeds() != null ? b.getTotalBeds() : 0).sum();
        Long availableBeds = buildings.stream().mapToLong(b -> b.getAvailableBeds() != null ? b.getAvailableBeds() : 0).sum();
        
        Long totalStudents = userMapper.selectCount(null);
        
        LambdaQueryWrapper<StudentAccommodation> accWrapper = new LambdaQueryWrapper<>();
        accWrapper.eq(StudentAccommodation::getStatus, "ACTIVE");
        Long checkedInStudents = accommodationMapper.selectCount(accWrapper);
        
        Double occupancyRate = totalBeds > 0 ? 
                (double) (totalBeds - availableBeds) / totalBeds * 100 : 0.0;
        
        Long totalRepairs = repairMapper.selectCount(null);
        
        LambdaQueryWrapper<RepairRequest> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(RepairRequest::getStatus, "PENDING");
        Long pendingRepairs = repairMapper.selectCount(pendingWrapper);
        
        LambdaQueryWrapper<RepairRequest> processingWrapper = new LambdaQueryWrapper<>();
        processingWrapper.eq(RepairRequest::getStatus, "PROCESSING");
        Long processingRepairs = repairMapper.selectCount(processingWrapper);
        
        LambdaQueryWrapper<RepairRequest> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(RepairRequest::getStatus, "COMPLETED");
        Long completedRepairs = repairMapper.selectCount(completedWrapper);
        
        Long totalInspections = hygieneMapper.selectCount(null);
        
        LambdaQueryWrapper<HygieneInspection> exWrapper = new LambdaQueryWrapper<>();
        exWrapper.eq(HygieneInspection::getGrade, "EXCELLENT");
        Long excellentCount = hygieneMapper.selectCount(exWrapper);
        
        LambdaQueryWrapper<HygieneInspection> goodWrapper = new LambdaQueryWrapper<>();
        goodWrapper.eq(HygieneInspection::getGrade, "GOOD");
        Long goodCount = hygieneMapper.selectCount(goodWrapper);
        
        LambdaQueryWrapper<HygieneInspection> qualWrapper = new LambdaQueryWrapper<>();
        qualWrapper.eq(HygieneInspection::getGrade, "QUALIFIED");
        Long qualifiedCount = hygieneMapper.selectCount(qualWrapper);
        
        LambdaQueryWrapper<HygieneInspection> unqualWrapper = new LambdaQueryWrapper<>();
        unqualWrapper.eq(HygieneInspection::getGrade, "UNQUALIFIED");
        Long unqualifiedCount = hygieneMapper.selectCount(unqualWrapper);
        
        Long totalFees = feeMapper.selectCount(null);
        
        LambdaQueryWrapper<Fee> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(Fee::getStatus, "PAID");
        Long paidFees = feeMapper.selectCount(paidWrapper);
        
        LambdaQueryWrapper<Fee> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(Fee::getStatus, "UNPAID");
        Long unpaidFees = feeMapper.selectCount(unpaidWrapper);
        
        Long totalVisitors = visitorMapper.selectCount(null);
        
        LambdaQueryWrapper<Visitor> visitingWrapper = new LambdaQueryWrapper<>();
        visitingWrapper.eq(Visitor::getStatus, "VISITING");
        Long visitingCount = visitorMapper.selectCount(visitingWrapper);
        
        Long totalViolations = 0L;
        
        return StatisticsResponse.builder()
                .totalBuildings(totalBuildings)
                .totalRooms(totalRooms)
                .totalBeds(totalBeds)
                .availableBeds(availableBeds)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .totalStudents(totalStudents)
                .checkedInStudents(checkedInStudents)
                .totalRepairs(totalRepairs)
                .pendingRepairs(pendingRepairs)
                .processingRepairs(processingRepairs)
                .completedRepairs(completedRepairs)
                .totalInspections(totalInspections)
                .excellentCount(excellentCount)
                .goodCount(goodCount)
                .qualifiedCount(qualifiedCount)
                .unqualifiedCount(unqualifiedCount)
                .totalFees(totalFees)
                .paidFees(paidFees)
                .unpaidFees(unpaidFees)
                .totalAmount(0.0)
                .paidAmount(0.0)
                .unpaidAmount(0.0)
                .totalVisitors(totalVisitors)
                .visitingCount(visitingCount)
                .totalViolations(totalViolations)
                .todayCheckIns(0L)
                .todayCheckOuts(0L)
                .todayRepairs(0L)
                .todayVisitors(0L)
                .build();
    }
    
    @Override
    public Map<String, Object> getOccupancyTrend() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> rates = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.toString());
            rates.add(Math.random() * 20 + 70);
        }
        
        result.put("dates", dates);
        result.put("rates", rates);
        return result;
    }
    
    @Override
    public Map<String, Object> getRepairTypeDistribution() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Long> distribution = new HashMap<>();
        
        distribution.put("水电维修", 45L);
        distribution.put("门窗维修", 23L);
        distribution.put("家具维修", 18L);
        distribution.put("空调维修", 14L);
        
        result.put("data", distribution);
        return result;
    }
    
    @Override
    public Map<String, Object> getHygieneScoreTrend() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.toString());
            scores.add(Math.random() * 10 + 85);
        }
        
        result.put("dates", dates);
        result.put("scores", scores);
        return result;
    }
    
    @Override
    public Map<String, Object> getBuildingOccupancy() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> buildingList = new ArrayList<>();
        
        List<Building> buildings = buildingMapper.selectList(null);
        for (Building building : buildings) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", building.getBuildingName());
            item.put("total", building.getTotalBeds());
            item.put("occupied", building.getTotalBeds() - building.getAvailableBeds());
            buildingList.add(item);
        }
        
        result.put("buildings", buildingList);
        return result;
    }
}
