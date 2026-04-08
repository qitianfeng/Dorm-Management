package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
    
    @Select("SELECT id FROM sys_dept WHERE parent_id = #{deptId}")
    List<Long> selectChildrenIdsByDeptId(Long deptId);
    
    @Select("SELECT parent_id FROM sys_dept WHERE id = #{deptId}")
    Long selectParentIdById(Long deptId);
}
