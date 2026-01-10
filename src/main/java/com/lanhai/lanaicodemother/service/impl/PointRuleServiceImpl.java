package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.mapper.PointRuleMapper;
import com.lanhai.lanaicodemother.model.dto.point.PointRuleUpdateRequest;
import com.lanhai.lanaicodemother.model.entity.PointRule;
import com.lanhai.lanaicodemother.model.enums.PointRuleKeyEnum;
import com.lanhai.lanaicodemother.model.vo.point.PointRuleVO;
import com.lanhai.lanaicodemother.service.PointRuleService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 积分规则 服务层实现。
 *
 * @author 积分系统
 */
@Slf4j
@Service
public class PointRuleServiceImpl extends ServiceImpl<PointRuleMapper, PointRule> implements PointRuleService {

    /**
     * 规则缓存Key前缀
     */
    private static final String RULE_CACHE_PREFIX = "point:rule:";
    /**
     * 规则缓存过期时间（小时）
     */
    private static final long RULE_CACHE_EXPIRE_HOURS = 1;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long getRuleValue(String ruleKey) {
        // 1. 先从Redis缓存获取
        String cacheKey = RULE_CACHE_PREFIX + ruleKey;
        String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            try {
                return Long.parseLong(cachedValue);
            } catch (NumberFormatException e) {
                log.warn("规则缓存值格式错误，ruleKey：{}，value：{}", ruleKey, cachedValue);
            }
        }

        // 2. 从数据库查询
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("rule_key", ruleKey);
        queryWrapper.eq("status", 1); // 只查询启用的规则
        PointRule rule = this.mapper.selectOneByQuery(queryWrapper);

        ThrowUtils.throwIf(rule == null, ErrorCode.NOT_FOUND_ERROR, "规则不存在或已禁用：" + ruleKey);

        Long ruleValue = rule.getRuleValue();

        // 3. 写入缓存
        stringRedisTemplate.opsForValue().set(cacheKey, ruleValue.toString(), RULE_CACHE_EXPIRE_HOURS, TimeUnit.HOURS);

        return ruleValue;
    }

    @Override
    public Long getRuleValue(PointRuleKeyEnum ruleKeyEnum) {
        if (ruleKeyEnum == null) {
            throw new IllegalArgumentException("规则键枚举不能为空");
        }
        return getRuleValue(ruleKeyEnum.getKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRule(PointRuleUpdateRequest updateRequest) {
        ThrowUtils.throwIf(updateRequest == null, ErrorCode.PARAMS_ERROR, "参数不能为空");

        // 1. 查询规则是否存在
        PointRule rule = this.getById(updateRequest.getId());
        ThrowUtils.throwIf(rule == null, ErrorCode.NOT_FOUND_ERROR, "规则不存在");

        // 2. 更新规则
        PointRule updateRule = new PointRule();
        updateRule.setId(updateRequest.getId());
        if (StrUtil.isNotBlank(updateRequest.getRuleKey())) {
            updateRule.setRuleKey(updateRequest.getRuleKey());
        }
        if (updateRequest.getRuleValue() != null) {
            updateRule.setRuleValue(updateRequest.getRuleValue());
        }
        if (StrUtil.isNotBlank(updateRequest.getRuleDesc())) {
            updateRule.setRuleDesc(updateRequest.getRuleDesc());
        }
        if (updateRequest.getStatus() != null) {
            updateRule.setStatus(updateRequest.getStatus());
        }

        boolean updated = this.updateById(updateRule);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新规则失败");

        // 3. 清除缓存
        clearRuleCache(rule.getRuleKey());
        if (StrUtil.isNotBlank(updateRequest.getRuleKey())) {
            clearRuleCache(updateRequest.getRuleKey());
        }

        log.info("更新积分规则成功，规则ID：{}，规则键：{}", updateRequest.getId(), rule.getRuleKey());
        return true;
    }

    @Override
    public List<PointRuleVO> getAllRules() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderBy("id", true);
        List<PointRule> rules = this.list(queryWrapper);

        return rules.stream()
                .map(rule -> {
                    PointRuleVO vo = new PointRuleVO();
                    BeanUtil.copyProperties(rule, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 清除规则缓存
     */
    private void clearRuleCache(String ruleKey) {
        stringRedisTemplate.delete(RULE_CACHE_PREFIX + ruleKey);
    }

}

