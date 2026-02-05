package com.lanhai.lanaicodemother.mapper;

import com.lanhai.lanaicodemother.model.entity.PointLog;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 积分流水 映射层。
 *
 * @author 积分系统
 */
public interface PointLogMapper extends BaseMapper<PointLog> {

    /**
     * 批量插入积分流水记录
     *
     * @param logs 流水记录列表
     * @return 插入行数
     */
    int batchInsert(@Param("logs") List<PointLog> logs);

}

