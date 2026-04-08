package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.StudentAccommodation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccommodationMapper extends BaseMapper<StudentAccommodation> {
}
