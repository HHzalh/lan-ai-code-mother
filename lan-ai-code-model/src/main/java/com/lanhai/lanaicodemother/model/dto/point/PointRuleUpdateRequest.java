package com.lanhai.lanaicodemother.model.dto.point;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 积分规则更新请求
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRuleUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空")
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

