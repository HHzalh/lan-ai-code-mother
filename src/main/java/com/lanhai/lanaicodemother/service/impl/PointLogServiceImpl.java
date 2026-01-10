package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.lanhai.lanaicodemother.mapper.PointLogMapper;
import com.lanhai.lanaicodemother.model.dto.point.PointLogQueryRequest;
import com.lanhai.lanaicodemother.model.entity.PointLog;
import com.lanhai.lanaicodemother.model.enums.PointBusinessTypeEnum;
import com.lanhai.lanaicodemother.model.enums.PointTypeEnum;
import com.lanhai.lanaicodemother.model.vo.point.PointLogVO;
import com.lanhai.lanaicodemother.service.PointLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分流水 服务层实现。
 *
 * @author 积分系统
 */
@Slf4j
@Service
public class PointLogServiceImpl extends ServiceImpl<PointLogMapper, PointLog> implements PointLogService {

    /**
     * 流水记录缓存Key前缀
     */
    private static final String LOG_CACHE_PREFIX = "point:log:";
    /**
     * 流水记录缓存过期时间（天）
     */
    private static final long LOG_CACHE_EXPIRE_DAYS = 7;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordLog(Long userId, String businessType, String businessId, String pointType,
                             Long pointChange, Long beforePoints, Long afterPoints, String remark) {
        PointLog log = new PointLog();
        log.setUserId(userId);
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setPointType(pointType);
        log.setPointChange(pointChange);
        log.setBeforePoints(beforePoints);
        log.setAfterPoints(afterPoints);
        log.setRemark(remark);
        log.setCreateTime(java.time.LocalDateTime.now());
        boolean saved = this.save(log);
        if (saved) {
            // 清除用户流水缓存
            clearLogCache(userId);
        }
        return saved;
    }

    @Override
    public Page<PointLogVO> pageLogs(Long userId, PointLogQueryRequest queryRequest) {
        // 构建查询条件
        QueryWrapper queryWrapper = new QueryWrapper();
        // 如果传入了userId则使用传入的（普通用户查自己的），否则从queryRequest获取（管理员查指定用户）
        // 管理员查询时，如果queryRequest.userId为null，则查询所有用户
        Long targetUserId = userId;
        if (targetUserId == null && queryRequest != null) {
            targetUserId = queryRequest.getUserId();
        }

        // 如果指定了userId（普通用户或管理员指定用户），则加上user_id条件
        // 如果userId为null（管理员未指定），则不限制user_id，查询所有用户
        if (targetUserId != null) {
            queryWrapper.eq("user_id", targetUserId);
        }

        if (queryRequest != null) {
            if (StrUtil.isNotBlank(queryRequest.getBusinessType())) {
                queryWrapper.eq("business_type", queryRequest.getBusinessType());
            }
            if (StrUtil.isNotBlank(queryRequest.getPointType())) {
                queryWrapper.eq("point_type", queryRequest.getPointType());
            }
            if (queryRequest.getStartTime() != null) {
                queryWrapper.ge("createTime", queryRequest.getStartTime());
            }
            if (queryRequest.getEndTime() != null) {
                queryWrapper.le("createTime", queryRequest.getEndTime());
            }
        }

        // 按时间倒序
        queryWrapper.orderBy("createTime", false);

        // 分页查询
        int pageSize = queryRequest != null ? queryRequest.getPageSize() : 10;
        int pageNum = queryRequest != null ? queryRequest.getPageNum() : 1;
        Page<PointLog> page = this.mapper.paginate(pageNum, pageSize, queryWrapper);

        // 转换为VO
        List<PointLogVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 创建新的Page对象
        Page<PointLogVO> voPage = new Page<>(page.getPageNumber(), page.getPageSize(), page.getTotalRow());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 转换为VO
     */
    private PointLogVO convertToVO(PointLog log) {
        PointLogVO vo = new PointLogVO();
        BeanUtil.copyProperties(log, vo);

        // 设置业务类型文本
        PointBusinessTypeEnum businessTypeEnum = PointBusinessTypeEnum.getEnumByValue(log.getBusinessType());
        vo.setBusinessTypeText(businessTypeEnum != null ? businessTypeEnum.getText() : log.getBusinessType());

        // 设置积分类型文本
        PointTypeEnum pointTypeEnum = PointTypeEnum.getEnumByValue(log.getPointType());
        vo.setPointTypeText(pointTypeEnum != null ? pointTypeEnum.getText() : log.getPointType());

        return vo;
    }

    /**
     * 清除流水缓存
     */
    private void clearLogCache(Long userId) {
        stringRedisTemplate.delete(LOG_CACHE_PREFIX + userId);
    }

}

