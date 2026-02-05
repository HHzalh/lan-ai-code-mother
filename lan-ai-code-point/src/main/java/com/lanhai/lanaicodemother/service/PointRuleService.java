package com.lanhai.lanaicodemother.service;

import com.lanhai.lanaicodemother.model.dto.point.PointRuleUpdateRequest;
import com.lanhai.lanaicodemother.model.entity.PointRule;
import com.lanhai.lanaicodemother.model.enums.PointRuleKeyEnum;
import com.lanhai.lanaicodemother.model.vo.point.PointRuleVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 积分规则 服务层。
 *
 * @author 积分系统
 */
public interface PointRuleService extends IService<PointRule> {

    /**
     * 根据规则键获取规则值
     *
     * @param ruleKey 规则键
     * @return 规则值
     */
    Long getRuleValue(String ruleKey);

    /**
     * 根据规则键枚举获取规则值
     *
     * @param ruleKeyEnum 规则键枚举
     * @return 规则值
     */
    Long getRuleValue(PointRuleKeyEnum ruleKeyEnum);

    /**
     * 更新积分规则
     *
     * @param updateRequest 更新请求
     * @return 是否成功
     */
    boolean updateRule(PointRuleUpdateRequest updateRequest);

    /**
     * 获取所有规则
     *
     * @return 规则列表
     */
    List<PointRuleVO> getAllRules();

}

