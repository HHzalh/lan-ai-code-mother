package com.lanhai.lanaicodemother.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分规则 实体类。
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("point_rule")
public class PointRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 规则键:SIGN_IN_BASE/SIGN_IN_CONTINUOUS_3/SIGN_IN_CONTINUOUS_7/REGISTER_REWARD/INVITE_NEW/INVITE_REWARD/DEPLOY_COST/AI_MESSAGE_COST/GENERATE_COST
     */
    @Column("rule_key")
    private String ruleKey;

    /**
     * 规则值(积分数)
     */
    @Column("rule_value")
    private Long ruleValue;

    /**
     * 规则描述
     */
    @Column("rule_desc")
    private String ruleDesc;

    /**
     * 状态:0-禁用 1-启用
     */
    @Column("status")
    private Integer status;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

}

