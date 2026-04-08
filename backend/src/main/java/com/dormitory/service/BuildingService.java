package com.dormitory.service;

import com.dormitory.common.PageResult;
import com.dormitory.dto.request.BuildingRequest;
import com.dormitory.dto.request.RoomRequest;
import com.dormitory.dto.response.BuildingResponse;
import com.dormitory.dto.response.RoomResponse;

import java.util.List;

/**
 * 宿舍楼服务接口
 */
public interface BuildingService {
    
    /**
     * 创建宿舍楼
     */
    BuildingResponse createBuilding(BuildingRequest request);
    
    /**
     * 更新宿舍楼
     */
    BuildingResponse updateBuilding(Long id, BuildingRequest request);
    
    /**
     * 删除宿舍楼
     */
    void deleteBuilding(Long id);
    
    /**
     * 获取宿舍楼详情
     */
    BuildingResponse getBuildingById(Long id);
    
    /**
     * 获取所有宿舍楼列表
     */
    List<BuildingResponse> getAllBuildings();
    
    /**
     * 分页查询宿舍楼
     */
    PageResult<BuildingResponse> getBuildingsPage(Integer page, Integer size, String keyword);
    
    /**
     * 创建房间
     */
    RoomResponse createRoom(RoomRequest request);
    
    /**
     * 更新房间
     */
    RoomResponse updateRoom(Long id, RoomRequest request);
    
    /**
     * 删除房间
     */
    void deleteRoom(Long id);
    
    /**
     * 获取房间详情
     */
    RoomResponse getRoomById(Long id);
    
    /**
     * 根据楼栋ID获取房间列表
     */
    List<RoomResponse> getRoomsByBuildingId(Long buildingId);
    
    /**
     * 分页查询房间
     */
    PageResult<RoomResponse> getRoomsPage(Integer page, Integer size, Long buildingId, String keyword);
}
