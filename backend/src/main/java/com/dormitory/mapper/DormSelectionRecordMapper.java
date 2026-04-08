package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.DormSelectionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选宿舍记录Mapper
 */
@Mapper
public interface DormSelectionRecordMapper extends BaseMapper<DormSelectionRecord> {
}
