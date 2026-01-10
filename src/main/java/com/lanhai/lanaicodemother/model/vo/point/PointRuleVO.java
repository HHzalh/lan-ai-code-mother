package com.lanhai.lanaicodemother.model.vo.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 积分规则 VO
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRuleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    private Long id;

    /**
     * 规则键
     */
    private String ruleKey;

    /**
     * 规则值
     */
    private Long ruleValue;

    /**
     * 规则描述
     */
    private String ruleDesc;

    /**
     * 状态
     */
    private Integer status;

}

