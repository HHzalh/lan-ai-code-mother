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
 * 积分流水 实体类。
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("point_log")
public class PointLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 用户ID
     */
    @Column("user_id")
    private Long userId;

    /**
     * 业务类型:SIGN_IN/SIGN_IN_BONUS/REGISTER_REWARD/INVITEE_BONUS/INVITER_BONUS/GENERATE/DEPLOY/REFUND/SYSTEM_GRANT等
     */
    @Column("business_type")
    private String businessType;

    /**
     * 业务ID(如应用ID、邀请码、被邀请人ID等)
     */
    @Column("business_id")
    private String businessId;

    /**
     * 积分类型:INCOME/EXPENSE
     */
    @Column("point_type")
    private String pointType;

    /**
     * 积分变动数(正数为增加,负数为减少)
     */
    @Column("point_change")
    private Long pointChange;

    /**
     * 变动前积分
     */
    @Column("before_points")
    private Long beforePoints;

    /**
     * 变动后积分
     */
    @Column("after_points")
    private Long afterPoints;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

}

