package com.lanhai.lanaicodemother.model.dto.point;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 积分规则添加请求
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRuleAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规则键
     */
    @NotBlank(message = "规则键不能为空")
    private String ruleKey;

    /**
     * 规则值
     */
    @NotNull(message = "规则值不能为空")
    private Long ruleValue;

    /**
     * 规则描述
     */
    @NotBlank(message = "规则描述不能为空")
    private String ruleDesc;

}

