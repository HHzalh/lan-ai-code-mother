package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lanhai.lanaicodemother.constant.PointConstants;
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
import java.util.Optional;
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
     * 规则缓存Key前缀（已废弃，保留用于兼容）
     *
     * @deprecated 使用 {@link PointConstants#ALL_RULES_CACHE_KEY} 代替
     */
    @Deprecated
    private static final String RULE_CACHE_PREFIX = "point:rule:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long getRuleValue(String ruleKey) {
        // 1. 尝试从 getAllRules() 缓存中获取（统一数据源）
        String cachedJson = stringRedisTemplate.opsForValue().get(PointConstants.ALL_RULES_CACHE_KEY);
        if (cachedJson != null) {
            try {
                List<PointRuleVO> rules = JSONUtil.toList(cachedJson, PointRuleVO.class);
                Optional<PointRuleVO> ruleOpt = rules.stream()
                        .filter(r -> ruleKey.equals(r.getRuleKey()))
                        .filter(r -> r.getStatus() != null && r.getStatus() == 1)  // 只返回启用的规则
                        .findFirst();

                if (ruleOpt.isPresent()) {
                    Long ruleValue = ruleOpt.get().getRuleValue();
                    log.debug("从缓存获取规则值，ruleKey：{}，value：{}", ruleKey, ruleValue);
                    return ruleValue;
                } else {
                    log.warn("缓存中找不到启用的规则：{}", ruleKey);
                }
            } catch (Exception e) {
                log.warn("从缓存解析规则失败，ruleKey：{}，错误：{}", ruleKey, e.getMessage());
            }
        }

        // 2. 缓存未命中或解析失败，从数据库查询
        log.info("缓存未命中，从数据库查询规则：{}", ruleKey);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("rule_key", ruleKey);
        queryWrapper.eq("status", 1); // 只查询启用的规则
        PointRule rule = this.mapper.selectOneByQuery(queryWrapper);

        ThrowUtils.throwIf(rule == null, ErrorCode.NOT_FOUND_ERROR, "规则不存在或已禁用：" + ruleKey);

        Long ruleValue = rule.getRuleValue();

        // 3. 异步刷新缓存（确保下次命中）
        try {
            getAllRules(); // 这会重新加载所有规则到缓存
            log.info("已刷新规则缓存");
        } catch (Exception e) {
            log.error("刷新规则缓存失败：{}", e.getMessage());
        }

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

        // 2. 使用 Builder 模式构建更新对象（遵循单一职责原则，只更新非空字段）
        PointRule.PointRuleBuilder builder = PointRule.builder().id(updateRequest.getId());

        if (StrUtil.isNotBlank(updateRequest.getRuleKey())) {
            builder.ruleKey(updateRequest.getRuleKey());
        }
        if (updateRequest.getRuleValue() != null) {
            builder.ruleValue(updateRequest.getRuleValue());
        }
        if (StrUtil.isNotBlank(updateRequest.getRuleDesc())) {
            builder.ruleDesc(updateRequest.getRuleDesc());
        }
        if (updateRequest.getStatus() != null) {
            builder.status(updateRequest.getStatus());
        }

        boolean updated = this.updateById(builder.build());
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新规则失败");

        // 3. 清除缓存（重试3次确保成功）
        for (int i = 0; i < 3; i++) {
            try {
                stringRedisTemplate.delete(PointConstants.ALL_RULES_CACHE_KEY);
                log.info("规则缓存已清除（第{}次尝试）", i + 1);
                break;
            } catch (Exception e) {
                log.warn("清除规则缓存失败，第{}次重试：{}", i + 1, e.getMessage());
                if (i == 2) {
                    log.error("清除规则缓存最终失败，可能导致数据不一致，建议手动清除Redis缓存：{}",
                            PointConstants.ALL_RULES_CACHE_KEY);
                }
            }
        }

        log.info("更新积分规则成功，规则ID：{}，规则键：{}", updateRequest.getId(), rule.getRuleKey());
        return true;
    }

    @Override
    public List<PointRuleVO> getAllRules() {
        // 1. 尝试从Redis缓存获取
        String cachedJson = stringRedisTemplate.opsForValue().get(PointConstants.ALL_RULES_CACHE_KEY);
        if (cachedJson != null) {
            try {
                List<PointRuleVO> rules = JSONUtil.toList(cachedJson, PointRuleVO.class);
                log.debug("从缓存获取所有规则，共{}条", rules.size());
                return rules;
            } catch (Exception e) {
                log.warn("解析规则缓存失败，将重新加载：{}", e.getMessage());
                // 解析失败时删除损坏的缓存
                stringRedisTemplate.delete(PointConstants.ALL_RULES_CACHE_KEY);
            }
        }

        // 2. 从数据库查询
        log.info("缓存未命中，从数据库加载所有规则");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderBy("id", true);
        List<PointRule> rules = this.list(queryWrapper);

        // 3. 转换为VO
        List<PointRuleVO> voList = rules.stream()
                .map(rule -> {
                    PointRuleVO vo = new PointRuleVO();
                    BeanUtil.copyProperties(rule, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        // 4. 写入Redis缓存（永不过期）
        try {
            String json = JSONUtil.toJsonStr(voList);
            stringRedisTemplate.opsForValue().set(PointConstants.ALL_RULES_CACHE_KEY, json);
            log.info("规则缓存已更新，共{}条规则", voList.size());
        } catch (Exception e) {
            log.error("写入规则缓存失败：{}", e.getMessage());
        }

        return voList;
    }

    /**
     * 清除规则缓存
     *
     * @deprecated 已废弃，现在使用统一缓存，只需清除 {@link PointConstants#ALL_RULES_CACHE_KEY}
     */
    @Deprecated
    private void clearRuleCache(String ruleKey) {
        stringRedisTemplate.delete(RULE_CACHE_PREFIX + ruleKey);
    }

}

