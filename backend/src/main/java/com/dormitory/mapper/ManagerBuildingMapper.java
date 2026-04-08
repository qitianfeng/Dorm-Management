package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.ManagerBuilding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ManagerBuildingMapper extends BaseMapper<ManagerBuilding> {
    
    /**
     * 根据用户ID查询管理的楼栋ID列表
     */
    @Select("SELECT building_id FROM dorm_manager_building WHERE user_id = #{userId}")
    List<Long> selectBuildingIdsByUserId(Long userId);
}
